package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.skill.UserSkillRequest;
import com.orgskills.intelligence.dto.skill.UserSkillResponse;
import com.orgskills.intelligence.service.UserSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final UserSkillService userSkillService;

    @GetMapping
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(userSkillService.getUserSkills(userId));
    }

    @PostMapping
    public ResponseEntity<UserSkillResponse> addSkill(@PathVariable Long userId,
                                                       @Valid @RequestBody UserSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userSkillService.addSkillToUser(userId, request));
    }

    @PutMapping("/{userSkillId}")
    public ResponseEntity<UserSkillResponse> updateSkill(@PathVariable Long userId,
                                                          @PathVariable Long userSkillId,
                                                          @Valid @RequestBody UserSkillRequest request) {
        return ResponseEntity.ok(userSkillService.updateUserSkill(userId, userSkillId, request));
    }

    @DeleteMapping("/{userSkillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long userId, @PathVariable Long userSkillId) {
        userSkillService.deleteUserSkill(userId, userSkillId);
        return ResponseEntity.noContent().build();
    }
}
