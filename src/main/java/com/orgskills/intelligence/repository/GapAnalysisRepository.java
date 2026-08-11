package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.enums.RiskSeverity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GapAnalysisRepository extends JpaRepository<GapAnalysis, Long> {
    List<GapAnalysis> findByUserIdOrderByGapScoreDesc(Long userId);

    List<GapAnalysis> findByUserIdIn(List<Long> userIds);

    List<GapAnalysis> findByUserIdAndRiskSeverity(Long userId, RiskSeverity riskSeverity);

    List<GapAnalysis> findByUserIdAndCurrentScoreEquals(Long userId, Double currentScore);

    List<GapAnalysis> findByUserIdAndCurrentScoreGreaterThanAndGapScoreGreaterThan(Long userId, Double currentScore, Double gapScore);

    void deleteByUserId(Long userId);

    @Query("SELECT AVG(g.gapScore) FROM GapAnalysis g WHERE g.user.id IN :userIds")
    Double getAverageGapScoreByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT g.riskSeverity, COUNT(g) FROM GapAnalysis g WHERE g.user.id IN :userIds GROUP BY g.riskSeverity")
    List<Object[]> countRiskSeverityByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT g.skill.name, AVG(g.gapScore) FROM GapAnalysis g WHERE g.user.id IN :userIds GROUP BY g.skill.name")
    List<Object[]> getAverageGapScoreBySkillForUsers(@Param("userIds") List<Long> userIds);

    @Query("SELECT AVG(g.gapScore) FROM GapAnalysis g")
    Double getOverallAverageGapScore();

    @Query("SELECT g.riskSeverity, COUNT(g) FROM GapAnalysis g GROUP BY g.riskSeverity")
    List<Object[]> countOverallRiskSeverity();

    @Query("SELECT g.user.department, AVG(g.gapScore) FROM GapAnalysis g GROUP BY g.user.department")
    List<Object[]> getAverageGapScoreByDepartment();

    @Query("SELECT SUM(g.targetScore) FROM GapAnalysis g")
    Double getTotalTargetScore();

    @Query("SELECT SUM(g.currentScore) FROM GapAnalysis g")
    Double getTotalCurrentScore();

    @Query("SELECT g.skill.id, g.skill.name, g.skill.category, COUNT(g) as cnt, AVG(g.gapScore) FROM GapAnalysis g WHERE g.currentScore = 0.0 GROUP BY g.skill.id, g.skill.name, g.skill.category ORDER BY cnt DESC")
    List<Object[]> getTopMissingSkills(Pageable pageable);
}
