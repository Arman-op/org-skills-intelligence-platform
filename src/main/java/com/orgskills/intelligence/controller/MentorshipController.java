package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.mentorship.MentorshipMatchRequest;
import com.orgskills.intelligence.dto.mentorship.MentorshipMatchResponse;
import com.orgskills.intelligence.dto.mentorship.MentorshipStatusUpdateRequest;
import com.orgskills.intelligence.service.MentorshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @PostMapping("/match")
    public ResponseEntity<MentorshipMatchResponse> match(@Valid @RequestBody MentorshipMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mentorshipService.createMatch(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MentorshipMatchResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorshipService.getMatchesByUser(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MentorshipMatchResponse> updateStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody MentorshipStatusUpdateRequest request) {
        return ResponseEntity.ok(mentorshipService.updateStatus(id, request.getStatus()));
    }
}
