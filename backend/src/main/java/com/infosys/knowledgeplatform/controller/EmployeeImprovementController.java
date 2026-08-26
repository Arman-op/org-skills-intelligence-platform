package com.infosys.knowledgeplatform.controller;

import com.infosys.knowledgeplatform.model.EmployeeImprovement;
import com.infosys.knowledgeplatform.repository.EmployeeImprovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee-improvements")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeImprovementController {

    @Autowired
    private EmployeeImprovementRepository employeeImprovementRepository;

    @GetMapping
    public List<EmployeeImprovement> getAllEmployeeImprovements() {
        return employeeImprovementRepository.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeImprovement getEmployeeImprovementById(@PathVariable Long id) {
        return employeeImprovementRepository.findById(id).orElse(null);
    }

    @PostMapping
    public EmployeeImprovement createOrUpdateEmployeeImprovement(@RequestBody EmployeeImprovement improvement) {
        return employeeImprovementRepository.findByEmployeeEmail(improvement.getEmployeeEmail())
                .map(existing -> {
                    existing.setEmployeeName(improvement.getEmployeeName());
                    existing.setRole(improvement.getRole());
                    existing.setTargetRole(improvement.getTargetRole());
                    existing.setOverallScore(improvement.getOverallScore());
                    existing.setGapSummary(improvement.getGapSummary());
                    existing.setEnrolledCourses(improvement.getEnrolledCourses());
                    existing.setImprovementSummary(improvement.getImprovementSummary());
                    return employeeImprovementRepository.save(existing);
                })
                .orElseGet(() -> employeeImprovementRepository.save(improvement));
    }

    @PutMapping("/{id}")
    public EmployeeImprovement updateEmployeeImprovement(@PathVariable Long id, @RequestBody EmployeeImprovement improvementDetails) {
        EmployeeImprovement improvement = employeeImprovementRepository.findById(id).orElse(null);
        if (improvement != null) {
            improvement.setEmployeeName(improvementDetails.getEmployeeName());
            improvement.setEmployeeEmail(improvementDetails.getEmployeeEmail());
            improvement.setRole(improvementDetails.getRole());
            improvement.setTargetRole(improvementDetails.getTargetRole());
            improvement.setOverallScore(improvementDetails.getOverallScore());
            improvement.setGapSummary(improvementDetails.getGapSummary());
            improvement.setEnrolledCourses(improvementDetails.getEnrolledCourses());
            improvement.setImprovementSummary(improvementDetails.getImprovementSummary());
            return employeeImprovementRepository.save(improvement);
        }
        return null;
    }
}