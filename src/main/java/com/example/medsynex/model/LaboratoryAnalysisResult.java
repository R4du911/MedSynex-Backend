package com.example.medsynex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "laboratory_results")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaboratoryAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_cnp")
    private Patient patient;

    private LocalDate createDate;
    private LocalDate updateDate;

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

    private String laboratoryName;

    private Boolean diabetesRisk;

    private String remarks;
}
