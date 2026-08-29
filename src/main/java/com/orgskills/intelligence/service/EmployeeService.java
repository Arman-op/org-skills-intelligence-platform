package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.employee.AchievementResponse;
import com.orgskills.intelligence.dto.employee.AssessmentRequest;
import com.orgskills.intelligence.dto.employee.AssessmentResponse;
import com.orgskills.intelligence.dto.employee.CertificationRequest;
import com.orgskills.intelligence.dto.employee.CertificationResponse;
import com.orgskills.intelligence.dto.employee.EmployeeProfileRequest;
import com.orgskills.intelligence.dto.employee.EmployeeProfileResponse;
import com.orgskills.intelligence.dto.employee.EnrollmentResponse;
import com.orgskills.intelligence.dto.employee.MentorMatchResponse;
import com.orgskills.intelligence.entity.Achievement;
import com.orgskills.intelligence.entity.Assessment;
import com.orgskills.intelligence.entity.Certification;
import com.orgskills.intelligence.entity.Course;
import com.orgskills.intelligence.entity.EmployeeProfile;
import com.orgskills.intelligence.entity.Enrollment;
import com.orgskills.intelligence.entity.MentorshipMatch;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.entity.enums.AchievementType;
import com.orgskills.intelligence.entity.enums.AssessmentType;
import com.orgskills.intelligence.entity.enums.CertificationStatus;
import com.orgskills.intelligence.entity.enums.EnrollmentStatus;
import com.orgskills.intelligence.entity.enums.MentorshipStatus;
import com.orgskills.intelligence.entity.enums.NotificationType;
import com.orgskills.intelligence.entity.enums.ProficiencyLevel;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.AchievementRepository;
import com.orgskills.intelligence.repository.AssessmentRepository;
import com.orgskills.intelligence.repository.CertificationRepository;
import com.orgskills.intelligence.repository.CourseRepository;
import com.orgskills.intelligence.repository.EmployeeProfileRepository;
import com.orgskills.intelligence.repository.EnrollmentRepository;
import com.orgskills.intelligence.repository.MentorshipMatchRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final AssessmentRepository assessmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AchievementRepository achievementRepository;
    private final CertificationRepository certificationRepository;
    private final MentorshipMatchRepository mentorshipMatchRepository;
    private final GapAnalysisService gapAnalysisService;
    private final RecommendationService recommendationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    @org.springframework.context.annotation.Lazy
    private final LearningPathService learningPathService;

    // ── Profile CRUD ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EmployeeProfileResponse getProfile(Long userId) {
        User user = getUser(userId);
        EmployeeProfile profile = employeeProfileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(user));
        return toProfileResponse(profile);
    }

    @Transactional
    public EmployeeProfileResponse updateProfile(Long userId, EmployeeProfileRequest request) {
        User user = getUser(userId);
        EmployeeProfile profile = employeeProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    EmployeeProfile p = new EmployeeProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setBio(request.getBio());
        profile.setDepartment(request.getDepartment());
        profile.setJobRole(request.getJobRole());
        profile.setWorkExperience(request.getWorkExperience());
        profile.setEducation(request.getEducation());

        EmployeeProfile saved = employeeProfileRepository.save(profile);
        auditLogService.logEvent(userId, user.getEmail(), "UPDATE_EMPLOYEE_PROFILE", "EmployeeProfile", saved.getId().toString(), "Employee profile updated");
        return toProfileResponse(saved);
    }

    // ── Self & Peer Assessment Chain Reactions ──────────────────────────────────

    @Transactional
    public AssessmentResponse submitSelfAssessment(Long userId, AssessmentRequest request) {
        User user = getUser(userId);
        Skill skill = getSkill(request.getSkillId());

        Assessment assessment = new Assessment();
        assessment.setType(AssessmentType.SELF);
        assessment.setSkill(skill);
        assessment.setScore(request.getScore());
        assessment.setSubmittedBy(user);
        assessment.setSubmittedFor(user);
        assessment.setComments(request.getComments());

        Assessment saved = assessmentRepository.save(assessment);

        // Update UserSkill
        updateUserSkillRating(user, skill, request.getScore());

        // Chain Reaction: Recalculate gaps & recommendations
        try {
            gapAnalysisService.calculateAndFetchUserGaps(userId);
        } catch (Exception ex) {
            log.warn("Gap calculation failed post self-assessment: {}", ex.getMessage());
        }

        auditLogService.logEvent(userId, user.getEmail(), "SUBMIT_SELF_ASSESSMENT", "Assessment", saved.getId().toString(), "Self assessment submitted for skill " + skill.getName());
        return toAssessmentResponse(saved);
    }

    @Transactional
    public AssessmentResponse submitPeerAssessment(Long submitterId, Long colleagueId, AssessmentRequest request) {
        if (submitterId.equals(colleagueId)) {
            throw new ValidationException("Cannot submit peer assessment for yourself. Use self-assessment.");
        }

        User submitter = getUser(submitterId);
        User colleague = getUser(colleagueId);
        Skill skill = getSkill(request.getSkillId());

        Assessment assessment = new Assessment();
        assessment.setType(AssessmentType.PEER);
        assessment.setSkill(skill);
        assessment.setScore(request.getScore());
        assessment.setSubmittedBy(submitter);
        assessment.setSubmittedFor(colleague);
        assessment.setComments(request.getComments());

        Assessment saved = assessmentRepository.save(assessment);

        // Calculate average peer score for this colleague & skill and update UserSkill
        List<Assessment> peerAssessments = assessmentRepository.findBySubmittedForIdAndSkillIdOrderBySubmittedAtDesc(colleagueId, skill.getId());
        double avgScore = peerAssessments.stream().mapToDouble(Assessment::getScore).average().orElse(request.getScore());
        updateUserSkillRating(colleague, skill, avgScore);

        // Chain Reaction for colleague
        try {
            gapAnalysisService.calculateAndFetchUserGaps(colleagueId);
        } catch (Exception ex) {
            log.warn("Gap calculation failed post peer assessment: {}", ex.getMessage());
        }

        // Notify colleague
        notificationService.createNotification(
                colleague,
                "Peer Assessment Received",
                submitter.getFullName() + " submitted a peer assessment for your " + skill.getName() + " skill.",
                NotificationType.INFO
        );

        auditLogService.logEvent(submitterId, submitter.getEmail(), "SUBMIT_PEER_ASSESSMENT", "Assessment", saved.getId().toString(), "Peer assessment submitted for " + colleague.getEmail());
        return toAssessmentResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> getAssessmentsReceived(Long userId) {
        return assessmentRepository.findBySubmittedForIdOrderBySubmittedAtDesc(userId).stream()
                .map(this::toAssessmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> getAssessmentsGiven(Long userId) {
        return assessmentRepository.findBySubmittedByIdOrderBySubmittedAtDesc(userId).stream()
                .map(this::toAssessmentResponse)
                .toList();
    }

    // ── Training Enrollment & Progress ──────────────────────────────────────────

    @Transactional
    public EnrollmentResponse enrollCourse(Long userId, Long courseId) {
        User employee = getUser(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found for id: " + courseId));

        Optional<Enrollment> existing = enrollmentRepository.findByEmployeeIdAndCourseId(userId, courseId);
        if (existing.isPresent()) {
            return toEnrollmentResponse(existing.get());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setEmployee(employee);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setProgressPercent(0.0);

        Enrollment saved = enrollmentRepository.save(enrollment);

        notificationService.createNotification(
                employee,
                "Course Enrollment Confirmed",
                "You have successfully enrolled in " + course.getTitle(),
                NotificationType.TRAINING_RECOMMENDATION
        );

        auditLogService.logEvent(userId, employee.getEmail(), "ENROLL_COURSE", "Enrollment", saved.getId().toString(), "Enrolled in course: " + course.getTitle());
        return toEnrollmentResponse(saved);
    }

    @Transactional
    public EnrollmentResponse updateProgress(Long userId, Long enrollmentId, Double progressPercent) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for id: " + enrollmentId));

        if (!enrollment.getEmployee().getId().equals(userId)) {
            throw new ValidationException("Access denied. Enrollment belongs to another employee.");
        }

        double newProgress = Math.max(0.0, Math.min(100.0, progressPercent));
        enrollment.setProgressPercent(newProgress);

        if (newProgress >= 100.0 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(Instant.now());

            // Auto-generate Achievement
            Achievement achievement = new Achievement();
            achievement.setEmployee(enrollment.getEmployee());
            achievement.setType(AchievementType.COURSE_COMPLETED);
            achievement.setTitle("Course Completed: " + enrollment.getCourse().getTitle());
            achievement.setDescription("Successfully completed 100% of " + enrollment.getCourse().getTitle());
            achievementRepository.save(achievement);

            // Adaptive recommendations: re-trigger recommendation service
            try {
                recommendationService.generateRecommendations(userId);
            } catch (Exception ex) {
                log.warn("Adaptive recommendation generation failed: {}", ex.getMessage());
            }

            // Learning path progress update: mark step completed
            try {
                learningPathService.onEnrollmentCompleted(userId, enrollment.getCourse().getId());
            } catch (Exception ex) {
                log.warn("Learning path step completion failed: {}", ex.getMessage());
            }

            notificationService.createNotification(
                    enrollment.getEmployee(),
                    "Achievement Unlocked!",
                    "Congratulations on completing " + enrollment.getCourse().getTitle() + "!",
                    NotificationType.SYSTEM_ALERT
            );
        } else if (newProgress > 0.0 && enrollment.getStatus() == EnrollmentStatus.NOT_STARTED) {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        return toEnrollmentResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(Long userId) {
        return enrollmentRepository.findByEmployeeId(userId).stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    // ── Achievements & Certifications ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAchievements(Long userId) {
        return achievementRepository.findByEmployeeIdOrderByEarnedAtDesc(userId).stream()
                .map(this::toAchievementResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CertificationResponse> getCertifications(Long userId) {
        return certificationRepository.findByEmployeeId(userId).stream()
                .map(this::toCertificationResponse)
                .toList();
    }

    @Transactional
    public CertificationResponse addCertification(Long userId, CertificationRequest request) {
        User employee = getUser(userId);

        Certification certification = new Certification();
        certification.setEmployee(employee);
        certification.setName(request.getName());
        certification.setIssuer(request.getIssuer());
        certification.setIssuedAt(request.getIssuedAt());
        certification.setExpiresAt(request.getExpiresAt());

        if (request.getExpiresAt() != null && request.getExpiresAt().isBefore(LocalDate.now().plusDays(30))) {
            certification.setStatus(CertificationStatus.EXPIRING_SOON);
        } else {
            certification.setStatus(CertificationStatus.ACTIVE);
        }

        Certification saved = certificationRepository.save(certification);

        // Auto-generate achievement for certification earned
        Achievement achievement = new Achievement();
        achievement.setEmployee(employee);
        achievement.setType(AchievementType.CERTIFICATION_EARNED);
        achievement.setTitle("Certification Earned: " + saved.getName());
        achievement.setDescription("Earned certification issued by " + saved.getIssuer());
        achievementRepository.save(achievement);

        auditLogService.logEvent(userId, employee.getEmail(), "ADD_CERTIFICATION", "Certification", saved.getId().toString(), "Added certification: " + saved.getName());
        return toCertificationResponse(saved);
    }

    // ── Mentorship ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MentorMatchResponse> getAvailableMentors(Long userId, Long skillId) {
        List<UserSkill> highlyProficient = userSkillRepository.findBySkillId(skillId).stream()
                .filter(us -> !us.getUser().getId().equals(userId))
                .filter(us -> us.getProficiencyLevel() == ProficiencyLevel.ADVANCED ||
                              us.getProficiencyLevel() == ProficiencyLevel.EXPERT ||
                              (us.getRatingScore() != null && us.getRatingScore() >= 4.0))
                .toList();

        return highlyProficient.stream().map(us -> MentorMatchResponse.builder()
                .mentorId(us.getUser().getId())
                .mentorName(us.getUser().getFullName())
                .mentorEmail(us.getUser().getEmail())
                .department(us.getUser().getDepartment())
                .jobTitle(us.getUser().getJobTitle())
                .skillId(us.getSkill().getId())
                .skillName(us.getSkill().getName())
                .proficiencyLevel(us.getProficiencyLevel())
                .ratingScore(us.getRatingScore())
                .build()).toList();
    }

    @Transactional
    public MentorshipMatch requestMentorship(Long menteeId, Long mentorId, Long targetSkillId) {
        if (menteeId.equals(mentorId)) {
            throw new ValidationException("Cannot request mentorship with yourself");
        }

        User mentee = getUser(menteeId);
        User mentor = getUser(mentorId);
        Skill targetSkill = getSkill(targetSkillId);

        MentorshipMatch match = new MentorshipMatch();
        match.setMentee(mentee);
        match.setMentor(mentor);
        match.setTargetSkill(targetSkill);
        match.setStatus(MentorshipStatus.REQUESTED);

        MentorshipMatch saved = mentorshipMatchRepository.save(match);

        notificationService.createNotification(
                mentor,
                "Mentorship Request",
                mentee.getFullName() + " has requested your mentorship for " + targetSkill.getName(),
                NotificationType.MENTORSHIP_REQUEST
        );

        return saved;
    }

    @Transactional
    public MentorshipMatch acceptMentorship(Long mentorId, Long matchId) {
        MentorshipMatch match = mentorshipMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship match not found for id: " + matchId));

        if (!match.getMentor().getId().equals(mentorId)) {
            throw new ValidationException("Access denied. You are not the assigned mentor.");
        }

        match.setStatus(MentorshipStatus.ACTIVE);
        MentorshipMatch saved = mentorshipMatchRepository.save(match);

        notificationService.createNotification(
                match.getMentee(),
                "Mentorship Request Accepted",
                match.getMentor().getFullName() + " accepted your mentorship request for " + match.getTargetSkill().getName(),
                NotificationType.INFO
        );

        return saved;
    }

    @Transactional
    public MentorshipMatch completeMentorship(Long userId, Long matchId) {
        MentorshipMatch match = mentorshipMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship match not found for id: " + matchId));

        if (!match.getMentee().getId().equals(userId) && !match.getMentor().getId().equals(userId)) {
            throw new ValidationException("Access denied. You are not a participant in this mentorship.");
        }

        match.setStatus(MentorshipStatus.COMPLETED);
        MentorshipMatch saved = mentorshipMatchRepository.save(match);

        Achievement achievement = new Achievement();
        achievement.setEmployee(match.getMentee());
        achievement.setType(AchievementType.MENTORSHIP_COMPLETED);
        achievement.setTitle("Mentorship Completed: " + match.getTargetSkill().getName());
        achievement.setDescription("Completed mentorship with " + match.getMentor().getFullName());
        achievementRepository.save(achievement);

        return saved;
    }

    // ── Helper methods ──────────────────────────────────────────────────────────

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + skillId));
    }

    private EmployeeProfile createEmptyProfile(User user) {
        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setDepartment(user.getDepartment());
        profile.setJobRole(user.getJobTitle());
        return employeeProfileRepository.save(profile);
    }

    private void updateUserSkillRating(User user, Skill skill, Double ratingScore) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skill.getId())
                .orElseGet(() -> {
                    UserSkill us = new UserSkill();
                    us.setUser(user);
                    us.setSkill(skill);
                    return us;
                });

        userSkill.setRatingScore(ratingScore);
        userSkill.setProficiencyLevel(scoreToProficiencyLevel(ratingScore));
        userSkillRepository.save(userSkill);
    }

    private ProficiencyLevel scoreToProficiencyLevel(double score) {
        if (score <= 1.0) return ProficiencyLevel.UNAWARE;
        if (score <= 2.0) return ProficiencyLevel.BEGINNER;
        if (score <= 3.0) return ProficiencyLevel.INTERMEDIATE;
        if (score <= 4.0) return ProficiencyLevel.ADVANCED;
        return ProficiencyLevel.EXPERT;
    }

    private EmployeeProfileResponse toProfileResponse(EmployeeProfile profile) {
        return EmployeeProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .userFullName(profile.getUser().getFullName())
                .userEmail(profile.getUser().getEmail())
                .bio(profile.getBio())
                .department(profile.getDepartment() != null ? profile.getDepartment() : profile.getUser().getDepartment())
                .jobRole(profile.getJobRole() != null ? profile.getJobRole() : profile.getUser().getJobTitle())
                .workExperience(profile.getWorkExperience())
                .education(profile.getEducation())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private AssessmentResponse toAssessmentResponse(Assessment a) {
        return AssessmentResponse.builder()
                .id(a.getId())
                .type(a.getType())
                .skillId(a.getSkill().getId())
                .skillName(a.getSkill().getName())
                .score(a.getScore())
                .submittedById(a.getSubmittedBy().getId())
                .submittedByName(a.getSubmittedBy().getFullName())
                .submittedForId(a.getSubmittedFor().getId())
                .submittedForName(a.getSubmittedFor().getFullName())
                .comments(a.getComments())
                .submittedAt(a.getSubmittedAt())
                .build();
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .employeeId(e.getEmployee().getId())
                .employeeName(e.getEmployee().getFullName())
                .courseId(e.getCourse().getId())
                .courseTitle(e.getCourse().getTitle())
                .provider(e.getCourse().getProvider())
                .status(e.getStatus())
                .progressPercent(e.getProgressPercent())
                .enrolledAt(e.getEnrolledAt())
                .completedAt(e.getCompletedAt())
                .build();
    }

    private AchievementResponse toAchievementResponse(Achievement a) {
        return AchievementResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .type(a.getType())
                .title(a.getTitle())
                .description(a.getDescription())
                .earnedAt(a.getEarnedAt())
                .build();
    }

    private CertificationResponse toCertificationResponse(Certification c) {
        return CertificationResponse.builder()
                .id(c.getId())
                .employeeId(c.getEmployee().getId())
                .employeeName(c.getEmployee().getFullName())
                .name(c.getName())
                .issuer(c.getIssuer())
                .issuedAt(c.getIssuedAt())
                .expiresAt(c.getExpiresAt())
                .status(c.getStatus())
                .build();
    }
}
