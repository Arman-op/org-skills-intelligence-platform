package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.model.User;
import com.infosys.knowledgeplatform.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getByEmail(@RequestParam(name = "email", required = false) String email) {
        if (email == null || email.isBlank()) return ResponseEntity.ok().build();
        Optional<User> u = userRepository.findByEmail(email);
        return u.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> updateByEmail(@RequestBody User user) {
        if (user.getEmail() == null) return ResponseEntity.badRequest().body("email required");
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            User e = existing.get();
            if (user.getName() != null) e.setName(user.getName());
            if (user.getTargetRole() != null) e.setTargetRole(user.getTargetRole());
            if (user.getDepartment() != null) e.setDepartment(user.getDepartment());
            userRepository.save(e);
            return ResponseEntity.ok(e);
        }
        return ResponseEntity.notFound().build();
    }
}
