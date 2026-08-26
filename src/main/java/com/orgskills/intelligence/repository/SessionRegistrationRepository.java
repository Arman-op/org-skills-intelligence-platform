package com.orgskills.intelligence.repository;

import com.orgskills.intelligence.entity.SessionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SessionRegistrationRepository extends JpaRepository<SessionRegistration, Long> {
    List<SessionRegistration> findBySessionIdOrderByRegisteredAtAsc(Long sessionId);

    List<SessionRegistration> findBySessionIdIn(Collection<Long> sessionIds);

    Optional<SessionRegistration> findBySessionIdAndEmployeeId(Long sessionId, Long employeeId);

    long countBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);
}
