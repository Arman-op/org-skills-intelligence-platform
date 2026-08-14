package com.orgskills.intelligence.controller;

import com.orgskills.intelligence.dto.ld.ExternalCourseResponse;
import com.orgskills.intelligence.service.ExternalCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final ExternalCatalogService externalCatalogService;

    @PostMapping("/import/provider/{providerName}")
    @PreAuthorize("hasAnyRole('LND_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<ExternalCourseResponse>> importFromProvider(
            @PathVariable String providerName,
            @RequestParam(required = false, defaultValue = "") String skill) {
        List<ExternalCourseResponse> imported = externalCatalogService.importFromProvider(providerName, skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }

    @PostMapping(value = "/import/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('LND_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<ExternalCourseResponse>> importFromFile(
            @RequestParam("file") MultipartFile file) {
        List<ExternalCourseResponse> imported = externalCatalogService.importFromFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }

    @GetMapping("/external")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'LND_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<ExternalCourseResponse>> getExternalCourses(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String provider) {
        List<ExternalCourseResponse> courses = externalCatalogService.getExternalCourses(skill, provider);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/external/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'DEPARTMENT_HEAD', 'HR_SPECIALIST', 'HR_ADMIN', 'LND_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<ExternalCourseResponse> getExternalCourseById(@PathVariable Long id) {
        ExternalCourseResponse course = externalCatalogService.getExternalCourseById(id);
        return ResponseEntity.ok(course);
    }
}
