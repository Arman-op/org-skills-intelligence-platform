package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    List<UserSkill> findBySkillIdAndProficiencyLevelOrderByRatingScoreDesc(Long skillId, ProficiencyLevel proficiencyLevel);
}
