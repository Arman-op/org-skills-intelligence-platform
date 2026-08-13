package com.orgskills.intelligence.dto.employee;

import com.orgskills.intelligence.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long courseId;
    private String courseTitle;
    private String provider;
    private EnrollmentStatus status;
    private Double progressPercent;
    private Instant enrolledAt;
    private Instant completedAt;
}
