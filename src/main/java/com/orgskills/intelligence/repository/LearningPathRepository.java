package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    List<LearningPath> findByTargetRoleIgnoreCaseOrTargetDepartmentIgnoreCase(String targetRole, String targetDepartment);
}
