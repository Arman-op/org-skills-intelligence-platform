package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.Role;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmployeeProfile employeeProfile;

    @OneToMany(mappedBy = "user")
    private List<UserSkill> userSkills = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<GapAnalysis> gapAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<TrainingRecommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "mentee")
    private List<MentorshipMatch> menteeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "mentor")
    private List<MentorshipMatch> mentorMatches = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Achievement> achievements = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    private List<Certification> certifications = new ArrayList<>();

    public User() {
    }

    public User(Long id, String email, String password, String fullName, Role role, String department, String jobTitle, String avatarUrl, Boolean active, User manager, EmployeeProfile employeeProfile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.department = department;
        this.jobTitle = jobTitle;
        this.avatarUrl = avatarUrl;
        this.active = active != null ? active : true;
        this.manager = manager;
        this.employeeProfile = employeeProfile;
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public EmployeeProfile getEmployeeProfile() {
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
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

    public List<TrainingRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<TrainingRecommendation> recommendations) {
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

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<Certification> certifications) {
        this.certifications = certifications;
    }
}
