package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByDepartmentIgnoreCase(String department);

    List<User> findByManagerId(Long managerId);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:department IS NULL OR LOWER(u.department) = LOWER(:department)) AND " +
           "(:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("query") String query,
                           @Param("department") String department,
                           @Param("role") Role role);

    long countByActiveTrue();
}
