package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.service.LearningPathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-plan")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class AIPlanController {

    private final LearningPathService learningPathService;

    public AIPlanController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @GetMapping
    public ResponseEntity<?> generate(
            @RequestParam(name = "role", defaultValue = "Employee") String role,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "limit", defaultValue = "5") int limit,
            @RequestHeader(name = "X-API-KEY", required = false) String apiKey
    ) {
        if (!learningPathService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(403).body(Map.of("message", "Invalid course API key"));
        }

        var dashboard = learningPathService.buildDashboard(role, email);
        List<Map<String, Object>> recs = learningPathService.generateCourseRecommendations(role, email, limit);

        // Build a simple rule-based textual plan
        StringBuilder plan = new StringBuilder();
        plan.append("AI Development Plan for ").append(role).append("\n\n");
        plan.append("Top priorities:\n");
        var gaps = (List<Map<String, Object>>) dashboard.getOrDefault("skillGaps", List.of());
        if (gaps.isEmpty()) {
            plan.append("- No major gaps detected. Focus on advanced projects and certifications.\n");
        } else {
            for (int i = 0; i < Math.min(3, gaps.size()); i++) {
                Map<String, Object> g = gaps.get(i);
                plan.append("- ").append(g.getOrDefault("skill", g.getOrDefault("title", "Skill"))).append(": focus on closing the ").append(g.getOrDefault("gap", "N/A")).append(" % gap.\n");
            }
        }

        plan.append("\nRecommended learning items:\n");
        if (recs.isEmpty()) {
            plan.append("- No recommendations available. Check training catalog.\n");
        } else {
            for (int i = 0; i < Math.min(limit, recs.size()); i++) {
                var r = recs.get(i);
                plan.append("- ").append(r.getOrDefault("title", "Course")).append(" (" ).append(r.getOrDefault("provider","" )).append(") - ").append(r.getOrDefault("matchScore", "--")).append("% match\n");
            }
        }

        plan.append("\nNext steps:\n- Enroll in top 1-2 courses.\n- Schedule hands-on practice.\n- Reassess in 4-6 weeks.\n");

        return ResponseEntity.ok(Map.of("role", role, "planText", plan.toString(), "recommendations", recs, "dashboard", dashboard));
    }
}
