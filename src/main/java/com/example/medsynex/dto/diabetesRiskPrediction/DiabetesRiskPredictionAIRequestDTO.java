package com.example.medsynex.dto.diabetesRiskPrediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DiabetesRiskPredictionAIRequestDTO {
    @JsonProperty("Pregnancies")
    private Integer pregnancies;

    @JsonProperty("Glucose")
    private Double glucose;

    @JsonProperty("BloodPressure")
    private Double bloodPressure;

    @JsonProperty("SkinThickness")
    private Double skinThickness;

    @JsonProperty("Insulin")
    private Double insulin;

    @JsonProperty("BMI")
    private Double bmi;

    @JsonProperty("DiabetesPedigreeFunction")
    private Double diabetesPedigreeFunction;

    @JsonProperty("Age")
    private Integer age;
}
