package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.gap.DepartmentGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.GapAnalysisResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GapAnalysisService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final RoleCompetencyRepository roleCompetencyRepository;
    private final GapAnalysisRepository gapAnalysisRepository;
    private final NotificationService notificationService;

    public GapAnalysisService(UserRepository userRepository,
                              UserSkillRepository userSkillRepository,
                              RoleCompetencyRepository roleCompetencyRepository,
                              GapAnalysisRepository gapAnalysisRepository,
                              NotificationService notificationService) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.roleCompetencyRepository = roleCompetencyRepository;
        this.gapAnalysisRepository = gapAnalysisRepository;
        this.notificationService = notificationService;
    }

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

        return savedGaps.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentGapMetricsResponse getDepartmentMetrics(String department) {
        List<User> users = userRepository.findByDepartmentIgnoreCase(department);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found for department: " + department);
        }

        List<Long> userIds = users.stream().map(User::getId).toList();
        List<GapAnalysis> gaps = gapAnalysisRepository.findByUserIdIn(userIds);
        if (gaps.isEmpty()) {
            throw new ValidationException("No gap analysis records exist for department: " + department
                    + ". Run user gap analysis first.");
        }

        Map<RiskSeverity, Long> severityCounts = new EnumMap<>(RiskSeverity.class);
        for (RiskSeverity severity : RiskSeverity.values()) {
            severityCounts.put(severity, 0L);
        }
        gaps.forEach(g -> severityCounts.put(g.getRiskSeverity(), severityCounts.get(g.getRiskSeverity()) + 1));

        Map<String, Double> skillAverages = gaps.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getSkill().getName(),
                        Collectors.averagingDouble(GapAnalysis::getGapScore)
                ));

        DepartmentGapMetricsResponse response = new DepartmentGapMetricsResponse();
        response.setDepartment(department);
        response.setEmployeeCount(users.size());
        response.setAverageGapScore(gaps.stream().mapToDouble(GapAnalysis::getGapScore).average().orElse(0.0));
        response.setSeverityDistribution(severityCounts.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue)));
        response.setSkillGapAverages(skillAverages);
        return response;
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
        GapAnalysisResponse response = new GapAnalysisResponse();
        response.setId(gap.getId());
        response.setUserId(gap.getUser().getId());
        response.setUserName(gap.getUser().getFullName());
        response.setSkillId(gap.getSkill().getId());
        response.setSkillName(gap.getSkill().getName());
        response.setTargetScore(gap.getTargetScore());
        response.setCurrentScore(gap.getCurrentScore());
        response.setGapScore(gap.getGapScore());
        response.setRiskSeverity(gap.getRiskSeverity());
        return response;
    }
}
