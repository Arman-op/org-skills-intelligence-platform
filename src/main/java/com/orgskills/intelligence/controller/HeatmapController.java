package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.heatmap.DepartmentHeatmapMatrixResponse;
import com.orgskills.intelligence.dto.heatmap.HeatmapMatrixResponse;
import com.orgskills.intelligence.service.HeatmapVisualizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/heatmap")
@RequiredArgsConstructor
public class HeatmapController {

    private final HeatmapVisualizationService heatmapVisualizationService;

    @GetMapping("/matrix")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<HeatmapMatrixResponse> getHeatmapMatrix(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(heatmapVisualizationService.getHeatmapMatrix(department, category));
    }

    @GetMapping("/department-matrix")
    @PreAuthorize("hasAnyRole('MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<DepartmentHeatmapMatrixResponse> getDepartmentHeatmapMatrix() {
        return ResponseEntity.ok(heatmapVisualizationService.getDepartmentHeatmapMatrix());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<HeatmapMatrixResponse> getUserHeatmap(@PathVariable Long userId) {
        return ResponseEntity.ok(heatmapVisualizationService.getUserHeatmap(userId));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getHeatmapSummary() {
        return ResponseEntity.ok(heatmapVisualizationService.getHeatmapSummaryMetrics());
    }
}
