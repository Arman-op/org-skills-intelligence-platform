package com.orgskills.intelligence.dto.gap;

import com.orgskills.intelligence.entity.enums.RiskSeverity;

public class GapAnalysisResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long skillId;
    private String skillName;
    private Double targetScore;
    private Double currentScore;
    private Double gapScore;
    private RiskSeverity riskSeverity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Double getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(Double targetScore) {
        this.targetScore = targetScore;
    }

    public Double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Double currentScore) {
        this.currentScore = currentScore;
    }

    public Double getGapScore() {
        return gapScore;
    }

    public void setGapScore(Double gapScore) {
        this.gapScore = gapScore;
    }

    public RiskSeverity getRiskSeverity() {
        return riskSeverity;
    }

    public void setRiskSeverity(RiskSeverity riskSeverity) {
        this.riskSeverity = riskSeverity;
    }
}
