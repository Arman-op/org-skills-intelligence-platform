package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String jobTitle;

    private String avatarUrl;

    @OneToMany(mappedBy = "user")
    private List<UserSkill> userSkills = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<GapAnalysis> gapAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "mentee")
    private List<MentorshipMatch> menteeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "mentor")
    private List<MentorshipMatch> mentorMatches = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<UserSkill> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<UserSkill> userSkills) {
        this.userSkills = userSkills;
    }

    public List<GapAnalysis> getGapAnalyses() {
        return gapAnalyses;
    }

    public void setGapAnalyses(List<GapAnalysis> gapAnalyses) {
        this.gapAnalyses = gapAnalyses;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<MentorshipMatch> getMenteeMatches() {
        return menteeMatches;
    }

    public void setMenteeMatches(List<MentorshipMatch> menteeMatches) {
        this.menteeMatches = menteeMatches;
    }

    public List<MentorshipMatch> getMentorMatches() {
        return mentorMatches;
    }

    public void setMentorMatches(List<MentorshipMatch> mentorMatches) {
        this.mentorMatches = mentorMatches;
    }
}
