package com.orgskills.intelligence.dto.recommendation;

import com.orgskills.intelligence.entity.enums.RecommendationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    private Long id;
    private Long userId;
    private Long gapId;
    private Long skillId;
    private String skillName;
    private String courseTitle;
    private String platform;
    private String url;
    private Double relevanceScore;
    private String aiReasoning;
    private RecommendationStatus status;
}
