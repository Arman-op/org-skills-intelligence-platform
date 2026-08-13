package com.orgskills.intelligence.dto.skill;

import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillRequest {
    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Proficiency level is required")
    private ProficiencyLevel proficiencyLevel;

    @NotNull(message = "Rating score is required")
    @Min(value = 0, message = "Rating score must be at least 0")
    @Max(value = 5, message = "Rating score must be at most 5")
    private Double ratingScore;
}
