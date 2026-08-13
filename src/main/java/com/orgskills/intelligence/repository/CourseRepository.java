package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findBySkillCoveredId(Long skillId);
    List<Course> findByIsInternal(Boolean isInternal);
}
