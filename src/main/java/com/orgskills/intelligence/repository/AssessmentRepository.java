package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findBySubmittedForIdOrderBySubmittedAtDesc(Long userId);
    List<Assessment> findBySubmittedByIdOrderBySubmittedAtDesc(Long userId);
    List<Assessment> findBySubmittedForIdAndSkillIdOrderBySubmittedAtDesc(Long userId, Long skillId);
}
