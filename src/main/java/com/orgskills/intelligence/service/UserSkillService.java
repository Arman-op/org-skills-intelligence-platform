package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.skill.UserSkillRequest;
import com.orgskills.intelligence.dto.skill.UserSkillResponse;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.UserSkill;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.SkillRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSkillService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    public List<UserSkillResponse> getUserSkills(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found for id: " + userId);
        }
        return userSkillRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserSkillResponse addSkillToUser(Long userId, UserSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + request.getSkillId()));

        userSkillRepository.findByUserIdAndSkillId(userId, request.getSkillId())
                .ifPresent(existing -> {
                    throw new ValidationException("User already has skill '" + skill.getName() + "' assigned");
                });

        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setProficiencyLevel(request.getProficiencyLevel());
        userSkill.setRatingScore(request.getRatingScore());

        return toResponse(userSkillRepository.save(userSkill));
    }

    @Transactional
    public UserSkillResponse updateUserSkill(Long userId, Long userSkillId, UserSkillRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found for id: " + userId);
        }
        UserSkill userSkill = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill not found for id: " + userSkillId));
        if (!userSkill.getUser().getId().equals(userId)) {
            throw new ValidationException("UserSkill does not belong to user " + userId);
        }

        userSkill.setProficiencyLevel(request.getProficiencyLevel());
        userSkill.setRatingScore(request.getRatingScore());
        return toResponse(userSkillRepository.save(userSkill));
    }

    @Transactional
    public void deleteUserSkill(Long userId, Long userSkillId) {
        UserSkill userSkill = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSkill not found for id: " + userSkillId));
        if (!userSkill.getUser().getId().equals(userId)) {
            throw new ValidationException("UserSkill does not belong to user " + userId);
        }
        userSkillRepository.delete(userSkill);
    }

    private UserSkillResponse toResponse(UserSkill userSkill) {
        return UserSkillResponse.builder()
                .id(userSkill.getId())
                .userId(userSkill.getUser().getId())
                .skillId(userSkill.getSkill().getId())
                .skillName(userSkill.getSkill().getName())
                .skillCategory(userSkill.getSkill().getCategory())
                .proficiencyLevel(userSkill.getProficiencyLevel())
                .ratingScore(userSkill.getRatingScore())
                .build();
    }
}
