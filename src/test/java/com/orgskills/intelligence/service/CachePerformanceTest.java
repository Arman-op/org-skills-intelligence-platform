package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.role.RoleCompetencyRequest;
import com.orgskills.intelligence.dto.skill.SkillRequest;
import com.orgskills.intelligence.entity.RoleCompetency;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.repository.RoleCompetencyRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CachePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(CachePerformanceTest.class);

    @Autowired
    private SkillService skillService;

    @Autowired
    private RoleCompetencyService roleCompetencyService;

    @SpyBean
    private SkillRepository skillRepository;

    @SpyBean
    private RoleCompetencyRepository roleCompetencyRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    private Skill savedSkill;
    private RoleCompetency savedCompetency;

    @BeforeEach
    void setUp() {
        // Clear caches and repository before tests to ensure isolated state
        if (cacheManager.getCache("skills") != null) cacheManager.getCache("skills").clear();
        if (cacheManager.getCache("competencies") != null) cacheManager.getCache("competencies").clear();
        
        jdbcTemplate.execute("TRUNCATE TABLE training_recommendations, gap_analyses, role_competencies, user_skills, skills, users CASCADE");

        // Seed some data so repository has something to return
        Skill s1 = new Skill();
        s1.setName("Cache Skill 1");
        s1.setCategory("Backend");
        savedSkill = skillRepository.save(s1);

        RoleCompetency rc1 = new RoleCompetency();
        rc1.setJobTitle("Developer");
        rc1.setDepartment("Engineering");
        rc1.setSkill(savedSkill);
        rc1.setRequiredProficiencyLevel(ProficiencyLevel.INTERMEDIATE);
        savedCompetency = roleCompetencyRepository.save(rc1);

        Mockito.reset(skillRepository, roleCompetencyRepository); // reset spy counts after setup
    }

    @AfterEach
    void tearDown() {
        if (cacheManager.getCache("skills") != null) cacheManager.getCache("skills").clear();
        if (cacheManager.getCache("competencies") != null) cacheManager.getCache("competencies").clear();
        roleCompetencyRepository.deleteAllInBatch();
        skillRepository.deleteAllInBatch();
    }

    @Test
    void benchmarkCacheHitsAndMisses() {
        StopWatch stopWatch = new StopWatch("Redis Cache Benchmark");

        // 1. Cache Miss (Cold Start)
        stopWatch.start("Cold Start (Cache Miss)");
        var initialResult = skillService.getAllSkills("Backend");
        stopWatch.stop();

        assertThat(initialResult).isNotEmpty();
        // Verify repository was called exactly once
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("Backend");
        logger.info("Cold Start Time: {} ms", stopWatch.getLastTaskTimeMillis());

        // 2. Cache Hit (Warm State)
        stopWatch.start("Warm State (100 Cache Hits)");
        for (int i = 0; i < 100; i++) {
            skillService.getAllSkills("Backend");
        }
        stopWatch.stop();

        // Verify repository was STILL only called once (0 additional times)
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("Backend");
        
        logger.info("Total Time for 100 Cache Hits: {} ms", stopWatch.getLastTaskTimeMillis());
        logger.info("Average Time per Cache Hit: {} ms", stopWatch.getLastTaskTimeMillis() / 100.0);
        logger.info("Validation successful: 100 subsequent requests served entirely from Redis.");
    }

    @Test
    void testCacheEvictionOnCreate() {
        skillService.getAllSkills("DevOps");
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("DevOps");

        skillService.getAllSkills("DevOps");
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("DevOps"); // cache hit

        SkillRequest newSkill = new SkillRequest();
        newSkill.setName("Docker Swarm");
        newSkill.setCategory("DevOps");
        skillService.create(newSkill);

        skillService.getAllSkills("DevOps");
        verify(skillRepository, times(2)).findByCategoryIgnoreCase("DevOps"); // cache miss
        logger.info("Validation successful: Cache was properly evicted upon new Skill creation.");
    }

    @Test
    void testCacheEvictionOnUpdate() {
        skillService.getAllSkills("Backend");
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("Backend");

        SkillRequest updateReq = new SkillRequest();
        updateReq.setName("Cache Skill 1 Updated");
        updateReq.setCategory("Backend");
        skillService.update(savedSkill.getId(), updateReq);

        skillService.getAllSkills("Backend");
        verify(skillRepository, times(2)).findByCategoryIgnoreCase("Backend");
        logger.info("Validation successful: Cache was properly evicted upon Skill update.");
    }

    @Test
    void testCacheEvictionOnDelete() {
        skillService.getAllSkills("Backend");
        verify(skillRepository, times(1)).findByCategoryIgnoreCase("Backend");

        // Delete RoleCompetency that references the Skill to prevent FK violation
        jdbcTemplate.execute("TRUNCATE TABLE role_competencies CASCADE");
        skillService.delete(savedSkill.getId());

        skillService.getAllSkills("Backend");
        verify(skillRepository, times(2)).findByCategoryIgnoreCase("Backend");
        logger.info("Validation successful: Cache was properly evicted upon Skill deletion.");
    }

    @Test
    @Transactional
    void testRoleCompetencyCache() {
        // Cold start
        roleCompetencyService.getCompetencies("Developer", "Engineering");
        verify(roleCompetencyRepository, times(1)).findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Developer", "Engineering");

        // Cache hit
        roleCompetencyService.getCompetencies("Developer", "Engineering");
        verify(roleCompetencyRepository, times(1)).findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Developer", "Engineering");

        // Eviction on create
        Skill s2 = new Skill();
        s2.setName("New Skill");
        s2.setCategory("Backend");
        s2 = skillRepository.save(s2);
        
        RoleCompetencyRequest rcReq = new RoleCompetencyRequest();
        rcReq.setJobTitle("Developer");
        rcReq.setDepartment("Engineering");
        rcReq.setSkillId(s2.getId());
        rcReq.setRequiredProficiencyLevel(ProficiencyLevel.EXPERT);
        roleCompetencyService.create(rcReq);

        // Cold start after eviction
        roleCompetencyService.getCompetencies("Developer", "Engineering");
        verify(roleCompetencyRepository, times(2)).findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Developer", "Engineering");
        
        logger.info("Validation successful: RoleCompetency caching and eviction verified.");
    }
}
