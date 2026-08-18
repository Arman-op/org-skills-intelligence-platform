package com.orgskills.intelligence.service;

import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.enums.RiskSeverity;
import com.orgskills.intelligence.entity.enums.Role;
import com.orgskills.intelligence.repository.GapAnalysisRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import com.orgskills.intelligence.repository.TrainingRecommendationRepository;
import com.orgskills.intelligence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        "logging.level.org.hibernate.SQL=DEBUG",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class GapAnalysisPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(GapAnalysisPerformanceTest.class);

    @Autowired
    private GapAnalysisService gapAnalysisService;

    @Autowired
    private GapAnalysisRepository gapAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE training_recommendations, gap_analyses, role_competencies, user_skills, skills, users CASCADE");

        // Populate a controlled dataset for aggregation validation
        User u1 = new User();
        u1.setFullName("Perf User 1");
        u1.setEmail("perf1@test.com");
        u1.setDepartment("Engineering");
        u1.setJobTitle("Engineer");
        u1.setRole(Role.EMPLOYEE);
        u1.setPassword("password123");
        userRepository.save(u1);

        User u2 = new User();
        u2.setFullName("Perf User 2");
        u2.setEmail("perf2@test.com");
        u2.setDepartment("Engineering");
        u2.setJobTitle("Engineer");
        u2.setRole(Role.EMPLOYEE);
        u2.setPassword("password123");
        userRepository.save(u2);

        Skill s1 = new Skill();
        s1.setName("Perf Skill 1");
        s1.setCategory("Backend");
        skillRepository.save(s1);

        Skill s2 = new Skill();
        s2.setName("Perf Skill 2");
        s2.setCategory("DevOps");
        skillRepository.save(s2);

        List<GapAnalysis> gaps = new ArrayList<>();
        // User 1 gaps
        gaps.add(new GapAnalysis(null, u1, s1, 5.0, 0.0, 5.0, RiskSeverity.CRITICAL, null));
        gaps.add(new GapAnalysis(null, u1, s2, 3.0, 1.0, 2.0, RiskSeverity.MEDIUM, null));
        // User 2 gaps
        gaps.add(new GapAnalysis(null, u2, s1, 5.0, 0.0, 5.0, RiskSeverity.CRITICAL, null));

        gapAnalysisRepository.saveAll(gaps);
        logger.info("Test dataset populated with 2 Users, 2 Skills, 3 GapAnalysis records.");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE training_recommendations, gap_analyses, role_competencies, user_skills, skills, users CASCADE");
    }

    @Test
    void benchmarkGetOrgGapMetrics() {
        StopWatch stopWatch = new StopWatch("OrgGapMetrics Benchmark");
        stopWatch.start("getOrgGapMetrics");
        
        var metrics = gapAnalysisService.getOrgGapMetrics();
        
        stopWatch.stop();
        
        assertThat(metrics.getTotalEmployees()).isEqualTo(2);
        assertThat(metrics.getTotalAnalyzedGaps()).isEqualTo(3);
        assertThat(metrics.getOverallAverageGapScore()).isEqualTo(4.0); // (5+2+5)/3
        
        logger.info("OrgGapMetrics Time: {} ms", stopWatch.getTotalTimeMillis());
        logger.info("Validation successful: Aggregation via PostgreSQL verified.");
    }

    @Test
    void benchmarkGetTopMissingSkills() {
        StopWatch stopWatch = new StopWatch("TopMissingSkills Benchmark");
        stopWatch.start("getTopMissingSkills");
        
        List<Object[]> topMissing = gapAnalysisRepository.getTopMissingSkills(PageRequest.of(0, 10));
        
        stopWatch.stop();
        
        assertThat(topMissing).isNotEmpty();
        assertThat(topMissing.get(0)[1]).isEqualTo("Perf Skill 1"); // s1 is missing for 2 users
        
        logger.info("TopMissingSkills Time: {} ms", stopWatch.getTotalTimeMillis());
        logger.info("Validation successful: GROUP BY and aggregation query verified.");
    }

    @Test
    void benchmarkGetAverageGapScoreByDepartment() {
        StopWatch stopWatch = new StopWatch("AvgGapScoreByDept Benchmark");
        stopWatch.start("getAverageGapScoreByDepartment");
        
        List<Object[]> deptAverages = gapAnalysisRepository.getAverageGapScoreByDepartment();
        
        stopWatch.stop();
        
        assertThat(deptAverages).isNotEmpty();
        assertThat(deptAverages.get(0)[0]).isEqualTo("Engineering");
        assertThat(deptAverages.get(0)[1]).isEqualTo(4.0);
        
        logger.info("AvgGapScoreByDept Time: {} ms", stopWatch.getTotalTimeMillis());
        logger.info("Validation successful: PostgreSQL AVG() and GROUP BY verified.");
    }
}
