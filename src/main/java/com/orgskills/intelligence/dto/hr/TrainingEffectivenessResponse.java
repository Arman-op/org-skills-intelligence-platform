package com.orgskills.intelligence.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingEffectivenessResponse {
    private Long courseId;
    private String courseTitle;
    private String provider;
    private String skillName;
    private Integer enrolledCount;
    private Integer completedCount;
    private Double completionRatePercent;
    private Double avgPreCourseSkillLevel;
    private Double avgPostCourseSkillLevel;
    private Double avgSkillImprovement;
}
