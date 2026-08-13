package com.orgskills.intelligence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_paths")
public class LearningPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    private String targetRole;

    private String targetDepartment;

    private String targetSeverity;

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    private List<LearningPathCourse> pathCourses = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public LearningPath() {
    }

    public LearningPath(Long id, String title, String description, String targetRole, String targetDepartment, String targetSeverity, List<LearningPathCourse> pathCourses, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetRole = targetRole;
        this.targetDepartment = targetDepartment;
        this.targetSeverity = targetSeverity;
        this.pathCourses = pathCourses != null ? pathCourses : new ArrayList<>();
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTargetDepartment() {
        return targetDepartment;
    }

    public void setTargetDepartment(String targetDepartment) {
        this.targetDepartment = targetDepartment;
    }

    public String getTargetSeverity() {
        return targetSeverity;
    }

    public void setTargetSeverity(String targetSeverity) {
        this.targetSeverity = targetSeverity;
    }

    public List<LearningPathCourse> getPathCourses() {
        return pathCourses;
    }

    public void setPathCourses(List<LearningPathCourse> pathCourses) {
        this.pathCourses = pathCourses;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
