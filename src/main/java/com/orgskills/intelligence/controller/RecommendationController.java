package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.recommendation.RecommendationResponse;
import com.orgskills.intelligence.dto.recommendation.RecommendationStatusUpdateRequest;
import com.orgskills.intelligence.service.OpenAiRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final OpenAiRecommendationService recommendationService;

    @PostMapping("/generate/{userId}")
    public ResponseEntity<List<RecommendationResponse>> generate(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.generateRecommendations(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RecommendationResponse> updateStatus(@PathVariable Long id,
                                                               @Valid @RequestBody RecommendationStatusUpdateRequest request) {
        return ResponseEntity.ok(recommendationService.updateStatus(id, request.getStatus()));
    }
}
