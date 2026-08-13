package com.orgskills.intelligence.entity;

import com.orgskills.intelligence.entity.enums.EnrollmentStatus;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.NOT_STARTED;

    @Column(nullable = false)
    private Double progressPercent = 0.0;

    @Column(nullable = false, updatable = false)
    private Instant enrolledAt;

    private Instant completedAt;

    @Column(nullable = false)
    private Instant lastProgressUpdateAt;

    public Enrollment() {
    }

    public Enrollment(Long id, User employee, Course course, EnrollmentStatus status, Double progressPercent, Instant enrolledAt, Instant completedAt, Instant lastProgressUpdateAt) {
        this.id = id;
        this.employee = employee;
        this.course = course;
        this.status = status;
        this.progressPercent = progressPercent;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
        this.lastProgressUpdateAt = lastProgressUpdateAt;
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.enrolledAt = now;
        this.lastProgressUpdateAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastProgressUpdateAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Double progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getLastProgressUpdateAt() {
        return lastProgressUpdateAt;
    }

    public void setLastProgressUpdateAt(Instant lastProgressUpdateAt) {
        this.lastProgressUpdateAt = lastProgressUpdateAt;
    }
}
