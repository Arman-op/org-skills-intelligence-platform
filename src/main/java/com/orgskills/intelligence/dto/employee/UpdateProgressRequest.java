package com.orgskills.intelligence.dto.employee;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    @NotNull(message = "Progress percent is required")
    @Min(value = 0, message = "Progress percent cannot be negative")
    @Max(value = 100, message = "Progress percent cannot exceed 100")
    private Double progressPercent;
}
