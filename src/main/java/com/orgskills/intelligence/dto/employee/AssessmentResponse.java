package com.orgskills.intelligence.dto.employee;

import com.orgskills.intelligence.entity.enums.AssessmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponse {
    private Long id;
    private AssessmentType type;
    private Long skillId;
    private String skillName;
    private Double score;
    private Long submittedById;
    private String submittedByName;
    private Long submittedForId;
    private String submittedForName;
    private String comments;
    private Instant submittedAt;
}
