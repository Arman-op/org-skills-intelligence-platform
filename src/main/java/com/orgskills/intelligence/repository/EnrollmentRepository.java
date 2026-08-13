package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.Enrollment;
import com.orgskills.intelligence.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByEmployeeId(Long employeeId);
    List<Enrollment> findByEmployeeIdIn(List<Long> employeeIds);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByEmployeeIdAndCourseId(Long employeeId, Long courseId);
    List<Enrollment> findByEmployeeIdAndStatus(Long employeeId, EnrollmentStatus status);
}
