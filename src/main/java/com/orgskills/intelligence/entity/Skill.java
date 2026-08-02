package com.orgskills.intelligence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "skill")
    private List<UserSkill> userSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    private List<RoleCompetency> roleCompetencies = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    private List<GapAnalysis> gapAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "targetSkill")
    private List<MentorshipMatch> mentorshipMatches = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserSkill> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<UserSkill> userSkills) {
        this.userSkills = userSkills;
    }

    public List<RoleCompetency> getRoleCompetencies() {
        return roleCompetencies;
    }

    public void setRoleCompetencies(List<RoleCompetency> roleCompetencies) {
        this.roleCompetencies = roleCompetencies;
    }

    public List<GapAnalysis> getGapAnalyses() {
        return gapAnalyses;
    }

    public void setGapAnalyses(List<GapAnalysis> gapAnalyses) {
        this.gapAnalyses = gapAnalyses;
    }

    public List<MentorshipMatch> getMentorshipMatches() {
        return mentorshipMatches;
    }

    public void setMentorshipMatches(List<MentorshipMatch> mentorshipMatches) {
        this.mentorshipMatches = mentorshipMatches;
    }
}
