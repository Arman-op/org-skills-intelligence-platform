package com.orgskills.intelligence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orgskills.intelligence.dto.recommendation.RecommendationResponse;
import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.Recommendation;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.enums.RecommendationStatus;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.repository.GapAnalysisRepository;
import com.orgskills.intelligence.repository.RecommendationRepository;
import com.orgskills.intelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiRecommendationService {

    private final UserRepository userRepository;
    private final GapAnalysisRepository gapAnalysisRepository;
    private final RecommendationRepository recommendationRepository;
    private final GapAnalysisService gapAnalysisService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${app.openai.api-key:}")
    private String openAiApiKey;

    @Value("${app.openai.model:gpt-4o-mini}")
    private String openAiModel;

    @Value("${app.openai.base-url:https://api.openai.com/v1/chat/completions}")
    private String openAiBaseUrl;

    @Transactional
    public List<RecommendationResponse> generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

        List<GapAnalysis> gaps = gapAnalysisRepository.findByUserIdOrderByGapScoreDesc(userId);
        if (gaps.isEmpty()) {
            gapAnalysisService.calculateAndFetchUserGaps(userId);
            gaps = gapAnalysisRepository.findByUserIdOrderByGapScoreDesc(userId);
        }

        List<RecommendationDraft> drafts = resolveRecommendationDrafts(user, gaps);
        recommendationRepository.deleteByUserId(userId);

        Map<Long, GapAnalysis> gapBySkill = new HashMap<>();
        for (GapAnalysis gap : gaps) {
            gapBySkill.put(gap.getSkill().getId(), gap);
        }

        List<Recommendation> recommendations = new ArrayList<>();
        for (RecommendationDraft draft : drafts) {
            GapAnalysis matchedGap = gapBySkill.get(draft.skillId());
            if (matchedGap == null && !gaps.isEmpty()) {
                matchedGap = gaps.get(0);
            }
            if (matchedGap == null) {
                continue;
            }

            Recommendation recommendation = new Recommendation();
            recommendation.setUser(user);
            recommendation.setGap(matchedGap);
            recommendation.setCourseTitle(draft.courseTitle());
            recommendation.setPlatform(draft.platform());
            recommendation.setUrl(draft.url());
            recommendation.setRelevanceScore(Math.max(0.0, Math.min(1.0, draft.relevanceScore())));
            recommendation.setAiReasoning(draft.aiReasoning());
            recommendation.setStatus(RecommendationStatus.RECOMMENDED);
            recommendations.add(recommendationRepository.save(recommendation));
        }

        return recommendations.stream().map(this::toResponse).toList();
    }

    @Transactional
    public RecommendationResponse updateStatus(Long recommendationId, RecommendationStatus status) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found for id: " + recommendationId));
        recommendation.setStatus(status);
        return toResponse(recommendationRepository.save(recommendation));
    }

    private List<RecommendationDraft> resolveRecommendationDrafts(User user, List<GapAnalysis> gaps) {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            return fallbackRecommendations(gaps);
        }
        try {
            return callOpenAi(user, gaps);
        } catch (Exception ex) {
            return fallbackRecommendations(gaps);
        }
    }

    private List<RecommendationDraft> callOpenAi(User user, List<GapAnalysis> gaps)
            throws IOException, InterruptedException {
        String prompt = buildPrompt(user, gaps);
        com.fasterxml.jackson.databind.node.ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put("model", openAiModel);
        requestJson.set("messages", objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode()
                        .put("role", "system")
                        .put("content", "You are an L&D strategist. Return only valid JSON array."))
                .add(objectMapper.createObjectNode()
                        .put("role", "user")
                        .put("content", prompt)));
        requestJson.put("temperature", 0.3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openAiBaseUrl))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestJson)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("OpenAI request failed with status " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        String content = contentNode.asText();
        return parseDrafts(content);
    }

    private String buildPrompt(User user, List<GapAnalysis> gaps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate 3-6 course recommendations for this employee.\n");
        sb.append("Employee: ").append(user.getFullName())
                .append(", job title: ").append(user.getJobTitle())
                .append(", department: ").append(user.getDepartment()).append("\n");
        sb.append("Skill gaps (skillId|skillName|gapScore|severity):\n");
        for (GapAnalysis gap : gaps.stream().sorted(Comparator.comparing(GapAnalysis::getGapScore).reversed()).toList()) {
            sb.append(gap.getSkill().getId()).append("|")
                    .append(gap.getSkill().getName()).append("|")
                    .append(String.format("%.2f", gap.getGapScore())).append("|")
                    .append(gap.getRiskSeverity().name()).append("\n");
        }
        sb.append("Return strict JSON array of objects with keys: ")
                .append("skillId (number), courseTitle (string), platform (string), url (string), relevanceScore (0-1), aiReasoning (string). ");
        sb.append("Do not include markdown fences or prose.");
        return sb.toString();
    }

    private List<RecommendationDraft> parseDrafts(String content) throws IOException {
        String normalized = content.trim();
        if (!normalized.startsWith("[")) {
            int start = normalized.indexOf('[');
            int end = normalized.lastIndexOf(']');
            if (start >= 0 && end > start) {
                normalized = normalized.substring(start, end + 1);
            }
        }

        JsonNode array = objectMapper.readTree(normalized);
        List<RecommendationDraft> drafts = new ArrayList<>();
        if (!array.isArray()) {
            return drafts;
        }

        for (JsonNode node : array) {
            long skillId = node.path("skillId").asLong(-1L);
            if (skillId <= 0) {
                continue;
            }
            String courseTitle = node.path("courseTitle").asText("").trim();
            String platform = node.path("platform").asText("").trim();
            String url = node.path("url").asText("").trim();
            String reasoning = node.path("aiReasoning").asText("").trim();
            double relevance = node.path("relevanceScore").asDouble(0.7);
            if (courseTitle.isEmpty() || platform.isEmpty() || url.isEmpty() || reasoning.isEmpty()) {
                continue;
            }
            drafts.add(new RecommendationDraft(skillId, courseTitle, platform, url, relevance, reasoning));
        }

        if (drafts.isEmpty()) {
            throw new IOException("AI response did not contain valid recommendations");
        }
        return drafts;
    }

    private List<RecommendationDraft> fallbackRecommendations(List<GapAnalysis> gaps) {
        List<RecommendationDraft> drafts = new ArrayList<>();
        for (GapAnalysis gap : gaps.stream().sorted(Comparator.comparing(GapAnalysis::getGapScore).reversed()).limit(6).toList()) {
            String skillName = gap.getSkill().getName();
            double relevance = Math.min(1.0, 0.55 + (gap.getGapScore() / 5.0));
            drafts.add(new RecommendationDraft(
                    gap.getSkill().getId(),
                    skillName + " Mastery Track",
                    "Internal LMS",
                    "https://lms.company.com/skills/" + skillName.toLowerCase().replace(" ", "-"),
                    relevance,
                    "Matched through fallback engine based on " + gap.getRiskSeverity().name()
                            + " severity and a gap score of " + String.format("%.2f", gap.getGapScore()) + "."
            ));
        }
        return drafts;
    }

    private RecommendationResponse toResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .userId(recommendation.getUser().getId())
                .gapId(recommendation.getGap().getId())
                .skillId(recommendation.getGap().getSkill().getId())
                .skillName(recommendation.getGap().getSkill().getName())
                .courseTitle(recommendation.getCourseTitle())
                .platform(recommendation.getPlatform())
                .url(recommendation.getUrl())
                .relevanceScore(recommendation.getRelevanceScore())
                .aiReasoning(recommendation.getAiReasoning())
                .status(recommendation.getStatus())
                .build();
    }

    private record RecommendationDraft(
            long skillId,
            String courseTitle,
            String platform,
            String url,
            double relevanceScore,
            String aiReasoning
    ) {
    }
}
