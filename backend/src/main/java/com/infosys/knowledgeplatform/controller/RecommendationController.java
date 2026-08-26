package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.service.LearningPathService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/learning-paths")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class RecommendationController {

    private final LearningPathService learningPathService;

    public RecommendationController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @GetMapping
    public ResponseEntity<?> recommend(
            @RequestParam(name = "role", defaultValue = "Employee") String role,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "limit", defaultValue = "5") int limit,
            @RequestHeader(name = "X-API-KEY", required = false) String apiKey
    ) {
        if (!learningPathService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Invalid course API key"));
        }

        Map<String, Object> dashboard = learningPathService.buildDashboard(role, email);

        return ResponseEntity.ok(Map.of(
                "role", role,
            "family", dashboard.get("roleFamily"),
            "roleProfile", dashboard.get("roleProfile"),
                "recommendedCourses", learningPathService.generateCourseRecommendations(role, email, limit),
            "dashboard", dashboard
        ));
    }
}