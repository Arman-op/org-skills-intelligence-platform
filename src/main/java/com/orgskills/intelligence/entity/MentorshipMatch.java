package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.MentorshipStatus;
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
@Table(name = "mentorship_matches")
public class MentorshipMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_skill_id", nullable = false)
    private Skill targetSkill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorshipStatus status;

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

    public User getMentee() {
        return mentee;
    }

    public void setMentee(User mentee) {
        this.mentee = mentee;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public Skill getTargetSkill() {
        return targetSkill;
    }

    public void setTargetSkill(Skill targetSkill) {
        this.targetSkill = targetSkill;
    }

    public MentorshipStatus getStatus() {
        return status;
    }

    public void setStatus(MentorshipStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
