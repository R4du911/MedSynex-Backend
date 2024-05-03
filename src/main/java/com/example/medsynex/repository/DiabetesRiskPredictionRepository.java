package com.example.medsynex.repository;

import com.example.medsynex.model.DiabetesRiskPredictionSavedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiabetesRiskPredictionRepository extends JpaRepository<DiabetesRiskPredictionSavedData, Long> {
    @Query("SELECT d FROM DiabetesRiskPredictionSavedData d WHERE d.patient.cnp = :cnp")
    DiabetesRiskPredictionSavedData findByPatient(Long cnp);
}
