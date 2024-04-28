package com.example.medsynex.dto.laboratoryAnalysisResult;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateLaboratoryAnalysisResultRequestDTO {
    private Double glucose;
    private Double insulin;
    private Double cholesterol;
    private Double triglyceride;
    private Double haemoglobin;
    private Double leukocyteCount;
    private Double plateletCount;
    private Double totalCalcium;
    private Double totalMagnesium;
    private Double serumIron;
    private Double serumSodium;
    private Double serumPotassium;
    private String laboratoryStaffUsername;
}
