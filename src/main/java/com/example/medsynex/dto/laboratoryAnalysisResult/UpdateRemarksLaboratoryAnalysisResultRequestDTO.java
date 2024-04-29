package com.example.medsynex.dto.laboratoryAnalysisResult;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRemarksLaboratoryAnalysisResultRequestDTO {
    private Long id;
    private String remarks;
}
