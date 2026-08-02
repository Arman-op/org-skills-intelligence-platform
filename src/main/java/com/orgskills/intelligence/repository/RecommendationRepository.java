package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByRelevanceScoreDesc(Long userId);

    void deleteByUserId(Long userId);
}
