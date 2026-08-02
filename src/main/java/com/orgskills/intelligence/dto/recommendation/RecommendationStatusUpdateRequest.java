package com.orgskills.intelligence.dto.recommendation;

import com.orgskills.intelligence.entity.enums.RecommendationStatus;
import jakarta.validation.constraints.NotNull;

public class RecommendationStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private RecommendationStatus status;

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }
}
