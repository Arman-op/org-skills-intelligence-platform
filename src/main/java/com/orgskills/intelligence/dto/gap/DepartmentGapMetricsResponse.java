package com.orgskills.intelligence.dto.gap;

import java.util.Map;

public class DepartmentGapMetricsResponse {
    private String department;
    private long employeeCount;
    private double averageGapScore;
    private Map<String, Long> severityDistribution;
    private Map<String, Double> skillGapAverages;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(long employeeCount) {
        this.employeeCount = employeeCount;
    }

    public double getAverageGapScore() {
        return averageGapScore;
    }

    public void setAverageGapScore(double averageGapScore) {
        this.averageGapScore = averageGapScore;
    }

    public Map<String, Long> getSeverityDistribution() {
        return severityDistribution;
    }

    public void setSeverityDistribution(Map<String, Long> severityDistribution) {
        this.severityDistribution = severityDistribution;
    }

    public Map<String, Double> getSkillGapAverages() {
        return skillGapAverages;
    }

    public void setSkillGapAverages(Map<String, Double> skillGapAverages) {
        this.skillGapAverages = skillGapAverages;
    }
}
