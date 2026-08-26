package com.orgskills.intelligence.dto.manager;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTrainingRequest {
    @NotNull(message = "Course ID is required")
    private Long courseId;
}
