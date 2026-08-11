package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.role.RoleCompetencyRequest;
import com.orgskills.intelligence.dto.role.RoleCompetencyResponse;
import com.orgskills.intelligence.entity.RoleCompetency;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.RoleCompetencyRepository;
import com.orgskills.intelligence.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleCompetencyService {

    private final RoleCompetencyRepository roleCompetencyRepository;
    private final SkillRepository skillRepository;

    @Cacheable(value = "competencies", key = "(#jobTitle != null ? #jobTitle : 'all') + ':' + (#department != null ? #department : 'all')")
    public List<RoleCompetencyResponse> getCompetencies(String jobTitle, String department) {
        List<RoleCompetency> competencies;
        if (jobTitle != null && !jobTitle.isBlank() && department != null && !department.isBlank()) {
            competencies = roleCompetencyRepository
                    .findByJobTitleIgnoreCaseAndDepartmentIgnoreCase(jobTitle.trim(), department.trim());
        } else {
            competencies = roleCompetencyRepository.findAll();
        }
        return competencies.stream().map(this::toResponse).toList();
    }

    @Transactional
    @CacheEvict(value = "competencies", allEntries = true)
    public RoleCompetencyResponse create(RoleCompetencyRequest request) {
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + request.getSkillId()));

        if (roleCompetencyRepository.existsByJobTitleIgnoreCaseAndDepartmentIgnoreCaseAndSkillId(
                request.getJobTitle().trim(), request.getDepartment().trim(), request.getSkillId())) {
            throw new ValidationException("Role competency already exists for this job title, department, and skill combination");
        }

        RoleCompetency competency = new RoleCompetency();
        competency.setJobTitle(request.getJobTitle().trim());
        competency.setDepartment(request.getDepartment().trim());
        competency.setSkill(skill);
        competency.setRequiredProficiencyLevel(request.getRequiredProficiencyLevel());

        return toResponse(roleCompetencyRepository.save(competency));
    }

    @Transactional
    @CacheEvict(value = "competencies", allEntries = true)
    public RoleCompetencyResponse update(Long id, RoleCompetencyRequest request) {
        RoleCompetency competency = roleCompetencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role competency not found for id: " + id));
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for id: " + request.getSkillId()));

        competency.setJobTitle(request.getJobTitle().trim());
        competency.setDepartment(request.getDepartment().trim());
        competency.setSkill(skill);
        competency.setRequiredProficiencyLevel(request.getRequiredProficiencyLevel());

        return toResponse(roleCompetencyRepository.save(competency));
    }

    @Transactional
    @CacheEvict(value = "competencies", allEntries = true)
    public void delete(Long id) {
        if (!roleCompetencyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role competency not found for id: " + id);
        }
        roleCompetencyRepository.deleteById(id);
    }

    private RoleCompetencyResponse toResponse(RoleCompetency competency) {
        return RoleCompetencyResponse.builder()
                .id(competency.getId())
                .jobTitle(competency.getJobTitle())
                .department(competency.getDepartment())
                .skillId(competency.getSkill().getId())
                .skillName(competency.getSkill().getName())
                .requiredProficiencyLevel(competency.getRequiredProficiencyLevel())
                .build();
    }
}
