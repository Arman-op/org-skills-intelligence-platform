package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.gap.DepartmentGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.GapAnalysisResponse;
import com.orgskills.intelligence.dto.gap.OrgGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.UserGapSummaryResponse;
import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.RoleCompetency;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.entity.enums.RiskSeverity;
import com.orgskills.intelligence.entity.enums.Role;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.GapAnalysisRepository;
import com.orgskills.intelligence.repository.RoleCompetencyRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GapAnalysisServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private RoleCompetencyRepository roleCompetencyRepository;

    @Mock
    private GapAnalysisRepository gapAnalysisRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private GapAnalysisService gapAnalysisService;

    private User sampleUser;
    private Skill javaSkill;
    private Skill dockerSkill;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setFullName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setJobTitle("Software Engineer");
        sampleUser.setDepartment("Engineering");
        sampleUser.setRole(Role.EMPLOYEE);

        javaSkill = new Skill();
        javaSkill.setId(10L);
        javaSkill.setName("Java");
        javaSkill.setCategory("Backend");

        dockerSkill = new Skill();
        dockerSkill.setId(20L);
        dockerSkill.setName("Docker");
        dockerSkill.setCategory("DevOps");
    }

    @Test
    @DisplayName("calculateAndFetchUserGaps identifies missing skills and proficiency gaps accurately")
    void testCalculateAndFetchUserGaps() {
        // User has Java at BEGINNER level (score 2.0), but no Docker skill record (Missing skill)
        RoleCompetency rc1 = new RoleCompetency(1L, "Software Engineer", "Engineering", javaSkill, ProficiencyLevel.EXPERT); // Target 5.0
        RoleCompetency rc2 = new RoleCompetency(2L, "Software Engineer", "Engineering", dockerSkill, ProficiencyLevel.INTERMEDIATE); // Target 3.0

        UserSkill userSkillJava = new UserSkill(100L, sampleUser, javaSkill, ProficiencyLevel.BEGINNER, 2.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(roleCompetencyRepository.findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Software Engineer", "Engineering"))
                .thenReturn(List.of(rc1, rc2));
        when(userSkillRepository.findByUserId(1L)).thenReturn(List.of(userSkillJava));
        doNothing().when(gapAnalysisRepository).deleteByUserId(1L);
        when(gapAnalysisRepository.save(any(GapAnalysis.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<GapAnalysisResponse> results = gapAnalysisService.calculateAndFetchUserGaps(1L);

        assertThat(results).hasSize(2);

        // First result should be highest gap score (Docker: target 3.0, current 0.0, gap 3.0 -> CRITICAL)
        // or Java (target 5.0, current 2.0, gap 3.0 -> CRITICAL)
        GapAnalysisResponse javaGap = results.stream().filter(g -> g.getSkillId().equals(10L)).findFirst().orElseThrow();
        assertThat(javaGap.getSkillName()).isEqualTo("Java");
        assertThat(javaGap.getTargetScore()).isEqualTo(5.0);
        assertThat(javaGap.getCurrentScore()).isEqualTo(2.0);
        assertThat(javaGap.getGapScore()).isEqualTo(3.0);
        assertThat(javaGap.isMissingSkill()).isFalse();
        assertThat(javaGap.getRiskSeverity()).isEqualTo(RiskSeverity.CRITICAL);

        GapAnalysisResponse dockerGap = results.stream().filter(g -> g.getSkillId().equals(20L)).findFirst().orElseThrow();
        assertThat(dockerGap.getSkillName()).isEqualTo("Docker");
        assertThat(dockerGap.getTargetScore()).isEqualTo(3.0);
        assertThat(dockerGap.getCurrentScore()).isEqualTo(0.0);
        assertThat(dockerGap.getGapScore()).isEqualTo(3.0);
        assertThat(dockerGap.isMissingSkill()).isTrue();
        assertThat(dockerGap.getRiskSeverity()).isEqualTo(RiskSeverity.CRITICAL);

        // Verification of database interactions and notifications for CRITICAL risk
        verify(gapAnalysisRepository).deleteByUserId(1L);
        verify(notificationService).createGapAlert(eq(sampleUser), eq(javaSkill), eq(3.0));
        verify(notificationService).createGapAlert(eq(sampleUser), eq(dockerSkill), eq(3.0));
    }

    @Test
    @DisplayName("calculateAndFetchUserGaps throws ValidationException when no role competencies exist")
    void testCalculateAndFetchUserGaps_NoCompetencies() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(roleCompetencyRepository.findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Software Engineer", "Engineering"))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> gapAnalysisService.calculateAndFetchUserGaps(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("No role competency profile found");
    }

    @Test
    @DisplayName("calculateAndFetchTargetRoleGaps compares against target role without deleting stored primary gaps")
    void testCalculateAndFetchTargetRoleGaps() {
        RoleCompetency rc = new RoleCompetency(1L, "Senior Architect", "Engineering", javaSkill, ProficiencyLevel.EXPERT);
        UserSkill userSkillJava = new UserSkill(100L, sampleUser, javaSkill, ProficiencyLevel.ADVANCED, 4.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(roleCompetencyRepository.findByJobTitleIgnoreCaseAndDepartmentIgnoreCase("Senior Architect", "Engineering"))
                .thenReturn(List.of(rc));
        when(userSkillRepository.findByUserId(1L)).thenReturn(List.of(userSkillJava));

        List<GapAnalysisResponse> results = gapAnalysisService.calculateAndFetchTargetRoleGaps(1L, "Senior Architect", "Engineering");

        assertThat(results).hasSize(1);
        GapAnalysisResponse gap = results.get(0);
        assertThat(gap.getTargetScore()).isEqualTo(5.0);
        assertThat(gap.getCurrentScore()).isEqualTo(4.0);
        assertThat(gap.getGapScore()).isEqualTo(1.0);
        assertThat(gap.getRiskSeverity()).isEqualTo(RiskSeverity.MEDIUM);
    }

    @Test
    @DisplayName("getUserGapSummary computes readiness percentage and risk breakdown correctly")
    void testGetUserGapSummary() {
        GapAnalysis gap1 = new GapAnalysis(1L, sampleUser, javaSkill, 5.0, 3.0, 2.0, RiskSeverity.HIGH, null);
        GapAnalysis gap2 = new GapAnalysis(2L, sampleUser, dockerSkill, 3.0, 0.0, 3.0, RiskSeverity.CRITICAL, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(gapAnalysisRepository.findByUserIdOrderByGapScoreDesc(1L)).thenReturn(List.of(gap2, gap1));

        UserGapSummaryResponse summary = gapAnalysisService.getUserGapSummary(1L);

        assertThat(summary.getUserId()).isEqualTo(1L);
        assertThat(summary.getTotalRequiredSkills()).isEqualTo(2);
        assertThat(summary.getMissingSkillsCount()).isEqualTo(1); // docker (currentScore 0)
        assertThat(summary.getProficiencyGapsCount()).isEqualTo(1); // java (currentScore 3 < target 5)
        assertThat(summary.getMetSkillsCount()).isEqualTo(0);
        // Total target = 8.0, Total current = 3.0, Readiness = (3/8)*100 = 37.5%
        assertThat(summary.getOverallReadinessPercentage()).isEqualTo(37.5);
    }

    @Test
    @DisplayName("getDepartmentMetrics computes average gap score and severity distribution")
    void testGetDepartmentMetrics() {
        GapAnalysis gap1 = new GapAnalysis(1L, sampleUser, javaSkill, 5.0, 3.0, 2.0, RiskSeverity.HIGH, null);

        when(userRepository.findByDepartmentIgnoreCase("Engineering")).thenReturn(List.of(sampleUser));
        when(gapAnalysisRepository.findByUserIdIn(List.of(1L))).thenReturn(List.of(gap1));

        DepartmentGapMetricsResponse metrics = gapAnalysisService.getDepartmentMetrics("Engineering");

        assertThat(metrics.getDepartment()).isEqualTo("Engineering");
        assertThat(metrics.getEmployeeCount()).isEqualTo(1);
        assertThat(metrics.getAverageGapScore()).isEqualTo(2.0);
        assertThat(metrics.getSeverityDistribution().get("HIGH")).isEqualTo(1L);
    }

    @Test
    @DisplayName("getOrgGapMetrics aggregates overall gap intelligence across organization")
    void testGetOrgGapMetrics() {
        GapAnalysis gap1 = new GapAnalysis(1L, sampleUser, javaSkill, 5.0, 0.0, 5.0, RiskSeverity.CRITICAL, null);

        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        when(gapAnalysisRepository.findAll()).thenReturn(List.of(gap1));

        OrgGapMetricsResponse orgMetrics = gapAnalysisService.getOrgGapMetrics();

        assertThat(orgMetrics.getTotalEmployees()).isEqualTo(1);
        assertThat(orgMetrics.getTotalAnalyzedGaps()).isEqualTo(1);
        assertThat(orgMetrics.getOverallAverageGapScore()).isEqualTo(5.0);
        assertThat(orgMetrics.getTopMissingSkills()).hasSize(1);
        assertThat(orgMetrics.getTopMissingSkills().get(0).getSkillName()).isEqualTo("Java");
    }
}
