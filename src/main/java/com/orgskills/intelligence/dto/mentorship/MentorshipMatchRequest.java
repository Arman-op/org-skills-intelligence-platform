package com.orgskills.intelligence.dto.mentorship;

import jakarta.validation.constraints.NotNull;

public class MentorshipMatchRequest {
    @NotNull(message = "Mentee id is required")
    private Long menteeId;

    @NotNull(message = "Skill id is required")
    private Long skillId;

    public Long getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(Long menteeId) {
        this.menteeId = menteeId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }
}
