package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.MentorshipMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipMatchRepository extends JpaRepository<MentorshipMatch, Long> {
    List<MentorshipMatch> findByMenteeIdOrMentorIdOrderByCreatedAtDesc(Long menteeId, Long mentorId);
}

