package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.skill.SkillRequest;
import com.orgskills.intelligence.dto.skill.SkillResponse;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<SkillResponse> getAllSkills(String category) {
        List<Skill> skills;
        if (category != null && !category.isBlank()) {
            skills = skillRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            skills = skillRepository.findAll();
        }
        return skills.stream().map(this::toResponse).toList();
    }

    public SkillResponse getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + id));
        return toResponse(skill);
    }

    @Transactional
    public SkillResponse create(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ValidationException("Skill with name '" + request.getName() + "' already exists");
        }
        Skill skill = new Skill();
        skill.setName(request.getName().trim());
        skill.setCategory(request.getCategory().trim());
        skill.setDescription(request.getDescription());
        return toResponse(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + id));
        skillRepository.findByNameIgnoreCase(request.getName().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ValidationException("Skill with name '" + request.getName() + "' already exists");
                });
        skill.setName(request.getName().trim());
        skill.setCategory(request.getCategory().trim());
        skill.setDescription(request.getDescription());
        return toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void delete(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found for id: " + id);
        }
        skillRepository.deleteById(id);
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .build();
    }
}
