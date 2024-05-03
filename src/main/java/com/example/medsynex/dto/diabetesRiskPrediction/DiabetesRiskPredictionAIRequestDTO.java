package com.example.medsynex.dto.diabetesRiskPrediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DiabetesRiskPredictionAIRequestDTO {
    private Integer Pregnancies;
    private Double Glucose;
    private Double BloodPressure;
    private Double SkinThickness;
    private Double Insulin;
    private Double BMI;
    private Double DiabetesPedigreeFunction;
    private Integer Age;
}
