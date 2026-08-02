package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.gap.DepartmentGapMetricsResponse;
import com.orgskills.intelligence.dto.gap.GapAnalysisResponse;
import com.orgskills.intelligence.service.GapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gaps")
@RequiredArgsConstructor
public class GapAnalysisController {

    private final GapAnalysisService gapAnalysisService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GapAnalysisResponse>> getUserGaps(@PathVariable Long userId) {
        return ResponseEntity.ok(gapAnalysisService.calculateAndFetchUserGaps(userId));
    }

    @GetMapping("/department/{departmentName}")
    public ResponseEntity<DepartmentGapMetricsResponse> getDepartmentMetrics(@PathVariable String departmentName) {
        return ResponseEntity.ok(gapAnalysisService.getDepartmentMetrics(departmentName));
    }
}
