package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.model.Article;
import com.infosys.knowledgeplatform.repository.ArticleRepository;
import com.infosys.knowledgeplatform.repository.UserRepository;
import com.infosys.knowledgeplatform.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

        @Autowired
        private LearningPathService learningPathService;

    @GetMapping("/dashboard")
        public Map<String, Object> getDashboardData(@RequestParam(required = false) String role, @RequestParam(required = false) String email) {
        Map<String, Object> response = new HashMap<>();
                response.putAll(learningPathService.buildDashboard(role, email));
                response.put("activeUsers", userRepository.count());
        response.put("recentArticles", articleRepository.findAll());

        return response;
    }
}
