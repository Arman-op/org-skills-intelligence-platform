package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.recommendation.RecommendationResponse;
import com.orgskills.intelligence.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Generates fresh AI-powered training recommendations from the employee's
     * current knowledge gaps. Deletes any previously stored recommendations
     * before saving the new set.
     */
    @PostMapping("/{employeeId}")
    public ResponseEntity<List<RecommendationResponse>> generate(@PathVariable Long employeeId) {
        return ResponseEntity.ok(recommendationService.generateRecommendations(employeeId));
    }

    /**
     * Returns the most recently generated training recommendations for the
     * given employee, ordered by priority rank (highest priority first).
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<RecommendationResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(recommendationService.getByEmployee(employeeId));
    }
}
