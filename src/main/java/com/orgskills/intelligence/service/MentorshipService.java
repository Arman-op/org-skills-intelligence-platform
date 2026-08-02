package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.mentorship.MentorshipMatchRequest;
import com.orgskills.intelligence.dto.mentorship.MentorshipMatchResponse;
import com.orgskills.intelligence.entity.GapAnalysis;
import com.orgskills.intelligence.entity.MentorshipMatch;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.MentorshipStatus;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.entity.enums.RiskSeverity;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.GapAnalysisRepository;
import com.orgskills.intelligence.repository.MentorshipMatchRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MentorshipService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final GapAnalysisRepository gapAnalysisRepository;
    private final MentorshipMatchRepository mentorshipMatchRepository;
    private final NotificationService notificationService;

    public MentorshipService(UserRepository userRepository,
                             SkillRepository skillRepository,
                             UserSkillRepository userSkillRepository,
                             GapAnalysisRepository gapAnalysisRepository,
                             MentorshipMatchRepository mentorshipMatchRepository,
                             NotificationService notificationService) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.gapAnalysisRepository = gapAnalysisRepository;
        this.mentorshipMatchRepository = mentorshipMatchRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public MentorshipMatchResponse createMatch(MentorshipMatchRequest request) {
        User mentee = userRepository.findById(request.getMenteeId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentee not found for id: " + request.getMenteeId()));
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + request.getSkillId()));

        List<GapAnalysis> criticalGaps = gapAnalysisRepository.findByUserIdAndRiskSeverity(mentee.getId(), RiskSeverity.CRITICAL);
        boolean hasCriticalForSkill = criticalGaps.stream().anyMatch(g -> g.getSkill().getId().equals(skill.getId()));
        if (!hasCriticalForSkill) {
            throw new ValidationException("Mentee does not have a CRITICAL gap for skill " + skill.getName());
        }

        List<UserSkill> expertCandidates = userSkillRepository
                .findBySkillIdAndProficiencyLevelOrderByRatingScoreDesc(skill.getId(), ProficiencyLevel.EXPERT);
        Optional<UserSkill> expert = expertCandidates.stream()
                .filter(candidate -> !candidate.getUser().getId().equals(mentee.getId()))
                .findFirst();

        if (expert.isEmpty()) {
            throw new ValidationException("No internal expert mentor available for skill " + skill.getName());
        }

        User mentor = expert.get().getUser();
        MentorshipMatch match = new MentorshipMatch();
        match.setMentee(mentee);
        match.setMentor(mentor);
        match.setTargetSkill(skill);
        match.setStatus(MentorshipStatus.PENDING);

        MentorshipMatch saved = mentorshipMatchRepository.save(match);

        notificationService.createMentorshipInvite(
                mentor,
                "You have a mentorship request from " + mentee.getFullName() + " for skill " + skill.getName() + "."
        );
        notificationService.createMentorshipInvite(
                mentee,
                "Mentor " + mentor.getFullName() + " has been matched for your " + skill.getName() + " development journey."
        );

        return toResponse(saved);
    }

    private MentorshipMatchResponse toResponse(MentorshipMatch match) {
        MentorshipMatchResponse response = new MentorshipMatchResponse();
        response.setId(match.getId());
        response.setMenteeId(match.getMentee().getId());
        response.setMenteeName(match.getMentee().getFullName());
        response.setMentorId(match.getMentor().getId());
        response.setMentorName(match.getMentor().getFullName());
        response.setSkillId(match.getTargetSkill().getId());
        response.setSkillName(match.getTargetSkill().getName());
        response.setStatus(match.getStatus());
        response.setCreatedAt(match.getCreatedAt());
        return response;
    }
}
