package com.example.medsynex.dto.diabetesRiskPrediction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiabetesRiskPredictionRequestDTO {
    private Long laboratoryAnalysisResultID;
    private Integer pregnancies;
    private Double glucose;
    private Double bloodPressure;
    private Double skinThickness;
    private Double insulin;
    private Double height;
    private Double weight;
    private Integer firstDegreeDiabetesCount;
    private Integer secondDegreeDiabetesCount;
    private Integer age;
}