package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.gap.DepartmentGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.GapAnalysisResponse;
import com.orgskills.intelligence.dto.gap.OrgGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.UserGapSummaryResponse;
import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.RoleCompetency;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.entity.enums.RiskSeverity;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.GapAnalysisRepository;
import com.orgskills.intelligence.repository.RoleCompetencyRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GapAnalysisService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final RoleCompetencyRepository roleCompetencyRepository;
    private final GapAnalysisRepository gapAnalysisRepository;
    private final NotificationService notificationService;
    private final RecommendationService recommendationService;

    @Transactional
    public List<GapAnalysisResponse> calculateAndFetchUserGaps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        List<RoleCompetency> requiredCompetencies = roleCompetencyRepository
                .findByJobTitleIgnoreCaseAndDepartmentIgnoreCase(user.getJobTitle(), user.getDepartment());
        if (requiredCompetencies.isEmpty()) {
            throw new ValidationException("No role competency profile found for " + user.getJobTitle() + " in " + user.getDepartment());
        }

        Map<Long, UserSkill> userSkillBySkillId = userSkillRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(us -> us.getSkill().getId(), Function.identity(), (a, b) -> a));

        gapAnalysisRepository.deleteByUserId(userId);

        List<GapAnalysis> savedGaps = requiredCompetencies.stream()
                .map(rc -> buildGap(user, rc, userSkillBySkillId.get(rc.getSkill().getId())))
                .sorted(Comparator.comparing(GapAnalysis::getGapScore).reversed())
                .map(gapAnalysisRepository::save)
                .toList();

        // Auto-regenerate recommendations whenever gaps are recalculated
        recommendationService.generateRecommendations(userId);

        return savedGaps.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<GapAnalysisResponse> calculateAndFetchTargetRoleGaps(Long userId, String targetJobTitle, String targetDepartment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        List<RoleCompetency> requiredCompetencies = roleCompetencyRepository
                .findByJobTitleIgnoreCaseAndDepartmentIgnoreCase(targetJobTitle, targetDepartment);
        if (requiredCompetencies.isEmpty()) {
            throw new ValidationException("No role competency profile found for target role: " + targetJobTitle + " in " + targetDepartment);
        }

        Map<Long, UserSkill> userSkillBySkillId = userSkillRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(us -> us.getSkill().getId(), Function.identity(), (a, b) -> a));

        return requiredCompetencies.stream()
                .map(rc -> buildGap(user, rc, userSkillBySkillId.get(rc.getSkill().getId())))
                .sorted(Comparator.comparing(GapAnalysis::getGapScore).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GapAnalysisResponse> getStoredUserGaps(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found for id: " + userId);
        }
        List<GapAnalysis> storedGaps = gapAnalysisRepository.findByUserIdOrderByGapScoreDesc(userId);
        if (storedGaps.isEmpty()) {
            return calculateAndFetchUserGaps(userId);
        }
        return storedGaps.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<GapAnalysisResponse> getMissingSkills(Long userId) {
        List<GapAnalysisResponse> allGaps = getStoredUserGaps(userId);
        return allGaps.stream()
                .filter(GapAnalysisResponse::isMissingSkill)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GapAnalysisResponse> getProficiencyGaps(Long userId) {
        List<GapAnalysisResponse> allGaps = getStoredUserGaps(userId);
        return allGaps.stream()
                .filter(g -> !g.isMissingSkill() && g.getGapScore() > 0.0)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserGapSummaryResponse getUserGapSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        List<GapAnalysisResponse> gaps = getStoredUserGaps(userId);
        int totalRequired = gaps.size();
        int missingCount = (int) gaps.stream().filter(GapAnalysisResponse::isMissingSkill).count();
        int proficiencyGapCount = (int) gaps.stream().filter(g -> !g.isMissingSkill() && g.getGapScore() > 0.0).count();
        int metCount = totalRequired - missingCount - proficiencyGapCount;

        double totalTargetScore = gaps.stream().mapToDouble(GapAnalysisResponse::getTargetScore).sum();
        double totalCurrentScore = gaps.stream().mapToDouble(GapAnalysisResponse::getCurrentScore).sum();
        double readiness = totalTargetScore > 0 ? Math.min(100.0, (totalCurrentScore / totalTargetScore) * 100.0) : 100.0;
        double avgGap = gaps.stream().mapToDouble(GapAnalysisResponse::getGapScore).average().orElse(0.0);

        Map<String, Long> riskMap = new java.util.LinkedHashMap<>();
        for (RiskSeverity r : RiskSeverity.values()) {
            riskMap.put(r.name(), 0L);
        }
        gaps.forEach(g -> riskMap.put(g.getRiskSeverity().name(), riskMap.get(g.getRiskSeverity().name()) + 1));

        List<GapAnalysisResponse> topCritical = gaps.stream()
                .filter(g -> g.getRiskSeverity() == RiskSeverity.CRITICAL || g.getRiskSeverity() == RiskSeverity.HIGH)
                .limit(5)
                .toList();

        return UserGapSummaryResponse.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .jobTitle(user.getJobTitle())
                .department(user.getDepartment())
                .totalRequiredSkills(totalRequired)
                .metSkillsCount(metCount)
                .missingSkillsCount(missingCount)
                .proficiencyGapsCount(proficiencyGapCount)
                .overallReadinessPercentage(Math.round(readiness * 100.0) / 100.0)
                .averageGapScore(Math.round(avgGap * 100.0) / 100.0)
                .riskDistribution(riskMap)
                .topCriticalGaps(topCritical)
                .build();
    }

    @Transactional(readOnly = true)
    public DepartmentGapMetricsResponse getDepartmentMetrics(String department) {
        List<User> users = userRepository.findByDepartmentIgnoreCase(department);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found for department: " + department);
        }

        List<Long> userIds = users.stream().map(User::getId).toList();
        
        Double avgScore = gapAnalysisRepository.getAverageGapScoreByUserIds(userIds);
        if (avgScore == null) {
            throw new ValidationException("No gap analysis records exist for department: " + department
                    + ". Run user gap analysis first.");
        }

        Map<RiskSeverity, Long> severityCounts = new EnumMap<>(RiskSeverity.class);
        for (RiskSeverity severity : RiskSeverity.values()) {
            severityCounts.put(severity, 0L);
        }
        
        List<Object[]> riskCounts = gapAnalysisRepository.countRiskSeverityByUserIds(userIds);
        for (Object[] row : riskCounts) {
            severityCounts.put((RiskSeverity) row[0], (Long) row[1]);
        }

        Map<String, Double> skillAverages = new java.util.HashMap<>();
        List<Object[]> skillAvgResults = gapAnalysisRepository.getAverageGapScoreBySkillForUsers(userIds);
        for (Object[] row : skillAvgResults) {
            skillAverages.put((String) row[0], (Double) row[1]);
        }

        return DepartmentGapMetricsResponse.builder()
                .department(department)
                .employeeCount(users.size())
                .averageGapScore(Math.round(avgScore * 100.0) / 100.0)
                .severityDistribution(severityCounts.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)))
                .skillGapAverages(skillAverages)
                .build();
    }

    @Transactional(readOnly = true)
    public OrgGapMetricsResponse getOrgGapMetrics() {
        long totalUsers = userRepository.count();
        long totalAnalyzedGaps = gapAnalysisRepository.count();

        if (totalAnalyzedGaps == 0) {
            return OrgGapMetricsResponse.builder()
                    .totalEmployees((int) totalUsers)
                    .totalAnalyzedGaps(0)
                    .overallAverageGapScore(0.0)
                    .overallReadinessPercentage(100.0)
                    .riskDistribution(Map.of())
                    .departmentAverageGaps(Map.of())
                    .topMissingSkills(List.of())
                    .build();
        }

        Double totalTarget = gapAnalysisRepository.getTotalTargetScore();
        Double totalCurrent = gapAnalysisRepository.getTotalCurrentScore();
        double readiness = (totalTarget != null && totalTarget > 0) ? Math.min(100.0, (totalCurrent / totalTarget) * 100.0) : 100.0;
        
        Double overallAvgGap = gapAnalysisRepository.getOverallAverageGapScore();

        Map<String, Long> riskMap = new java.util.LinkedHashMap<>();
        for (RiskSeverity r : RiskSeverity.values()) {
            riskMap.put(r.name(), 0L);
        }
        
        List<Object[]> riskCounts = gapAnalysisRepository.countOverallRiskSeverity();
        for (Object[] row : riskCounts) {
            riskMap.put(((RiskSeverity) row[0]).name(), (Long) row[1]);
        }

        Map<String, Double> deptAverages = new java.util.HashMap<>();
        List<Object[]> deptResults = gapAnalysisRepository.getAverageGapScoreByDepartment();
        for (Object[] row : deptResults) {
            deptAverages.put((String) row[0], (Double) row[1]);
        }

        List<Object[]> missingSkills = gapAnalysisRepository.getTopMissingSkills(PageRequest.of(0, 10));
        List<OrgGapMetricsResponse.SkillGapSummary> topMissing = missingSkills.stream()
                .map(row -> OrgGapMetricsResponse.SkillGapSummary.builder()
                        .skillId((Long) row[0])
                        .skillName((String) row[1])
                        .category((String) row[2])
                        .affectedEmployeesCount(((Number) row[3]).intValue())
                        .averageGapScore(Math.round(((Double) row[4]) * 100.0) / 100.0)
                        .build())
                .toList();

        return OrgGapMetricsResponse.builder()
                .totalEmployees((int) totalUsers)
                .totalAnalyzedGaps((int) totalAnalyzedGaps)
                .overallAverageGapScore(Math.round((overallAvgGap != null ? overallAvgGap : 0.0) * 100.0) / 100.0)
                .overallReadinessPercentage(Math.round(readiness * 100.0) / 100.0)
                .riskDistribution(riskMap)
                .departmentAverageGaps(deptAverages)
                .topMissingSkills(topMissing)
                .build();
    }

    private GapAnalysis buildGap(User user, RoleCompetency roleCompetency, UserSkill userSkill) {
        double target = proficiencyToScore(roleCompetency.getRequiredProficiencyLevel());
        double current = userSkill == null ? 0.0 : normalizedCurrentScore(userSkill);
        double gapScore = Math.max(0.0, target - current);
        RiskSeverity severity = classifyRisk(gapScore);

        GapAnalysis gap = new GapAnalysis();
        gap.setUser(user);
        gap.setSkill(roleCompetency.getSkill());
        gap.setTargetScore(target);
        gap.setCurrentScore(current);
        gap.setGapScore(gapScore);
        gap.setRiskSeverity(severity);

        if (severity == RiskSeverity.CRITICAL) {
            notificationService.createGapAlert(user, roleCompetency.getSkill(), gapScore);
        }
        return gap;
    }

    private double normalizedCurrentScore(UserSkill userSkill) {
        if (userSkill.getRatingScore() != null) {
            return Math.max(0.0, Math.min(5.0, userSkill.getRatingScore()));
        }
        return proficiencyToScore(userSkill.getProficiencyLevel());
    }

    private double proficiencyToScore(ProficiencyLevel level) {
        return switch (level) {
            case UNAWARE -> 1.0;
            case BEGINNER -> 2.0;
            case INTERMEDIATE -> 3.0;
            case ADVANCED -> 4.0;
            case EXPERT -> 5.0;
        };
    }

    private String scoreToProficiencyLabel(double score) {
        if (score <= 0.0) return "NONE";
        if (score <= 1.0) return ProficiencyLevel.UNAWARE.name();
        if (score <= 2.0) return ProficiencyLevel.BEGINNER.name();
        if (score <= 3.0) return ProficiencyLevel.INTERMEDIATE.name();
        if (score <= 4.0) return ProficiencyLevel.ADVANCED.name();
        return ProficiencyLevel.EXPERT.name();
    }

    private RiskSeverity classifyRisk(double gapScore) {
        if (gapScore >= 3.0) {
            return RiskSeverity.CRITICAL;
        }
        if (gapScore >= 2.0) {
            return RiskSeverity.HIGH;
        }
        if (gapScore >= 1.0) {
            return RiskSeverity.MEDIUM;
        }
        return RiskSeverity.LOW;
    }

    public GapAnalysisResponse toResponse(GapAnalysis gap) {
        boolean missing = gap.getCurrentScore() == 0.0;
        return GapAnalysisResponse.builder()
                .id(gap.getId())
                .userId(gap.getUser().getId())
                .userName(gap.getUser().getFullName())
                .skillId(gap.getSkill().getId())
                .skillName(gap.getSkill().getName())
                .skillCategory(gap.getSkill().getCategory())
                .targetScore(gap.getTargetScore())
                .currentScore(gap.getCurrentScore())
                .gapScore(gap.getGapScore())
                .targetProficiency(scoreToProficiencyLabel(gap.getTargetScore()))
                .currentProficiency(scoreToProficiencyLabel(gap.getCurrentScore()))
                .isMissingSkill(missing)
                .riskSeverity(gap.getRiskSeverity())
                .build();
    }
}
