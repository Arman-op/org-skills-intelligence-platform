package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "role_competencies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_dept_skill", columnNames = {"job_title", "department", "skill_id"})
})
public class RoleCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "job_title")
    private String jobTitle;

    @Column(nullable = false)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProficiencyLevel requiredProficiencyLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getRequiredProficiencyLevel() {
        return requiredProficiencyLevel;
    }

    public void setRequiredProficiencyLevel(ProficiencyLevel requiredProficiencyLevel) {
        this.requiredProficiencyLevel = requiredProficiencyLevel;
    }
}
