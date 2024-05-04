package com.example.medsynex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diabetes_risk_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiabetesRiskPredictionSavedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_cnp")
    private Patient patient;

    private Integer pregnancies;
    private Double skinThickness;
    private Integer firstDegreeDiabetesCount;
    private Integer secondDegreeDiabetesCount;
    private Integer age;
}
