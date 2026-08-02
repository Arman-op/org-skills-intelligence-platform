package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.RiskSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "gap_analyses")
public class GapAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Double targetScore;

    @Column(nullable = false)
    private Double currentScore;

    @Column(nullable = false)
    private Double gapScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskSeverity riskSeverity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
