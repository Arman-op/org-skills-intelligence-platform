package com.orgskills.intelligence.dto.employee;

import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorMatchResponse {
    private Long mentorId;
    private String mentorName;
    private String mentorEmail;
    private String department;
    private String jobTitle;
    private Long skillId;
    private String skillName;
    private ProficiencyLevel proficiencyLevel;
    private Double ratingScore;
}
