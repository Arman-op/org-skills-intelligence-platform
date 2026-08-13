package com.orgskills.intelligence.dto.ld;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathResponse {
    private Long id;
    private String title;
    private String description;
    private String targetRole;
    private String targetDepartment;
    private String targetSeverity;
    private List<CourseResponse> courses;
    private Instant createdAt;
}
