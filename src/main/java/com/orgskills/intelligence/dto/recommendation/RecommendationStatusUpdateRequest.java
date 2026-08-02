package com.orgskills.intelligence.dto.recommendation;

import com.orgskills.intelligence.entity.enums.RecommendationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private RecommendationStatus status;
}
