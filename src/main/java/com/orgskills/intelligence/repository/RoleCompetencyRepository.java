package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.RoleCompetency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleCompetencyRepository extends JpaRepository<RoleCompetency, Long> {
    List<RoleCompetency> findByJobTitleIgnoreCaseAndDepartmentIgnoreCase(String jobTitle, String department);

    boolean existsByJobTitleIgnoreCaseAndDepartmentIgnoreCaseAndSkillId(String jobTitle, String department, Long skillId);
}
