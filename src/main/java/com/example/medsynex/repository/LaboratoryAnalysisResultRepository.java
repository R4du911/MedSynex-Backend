package com.example.medsynex.repository;

import com.example.medsynex.model.LaboratoryAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryAnalysisResultRepository extends JpaRepository<LaboratoryAnalysisResult, Long> {
    @Query("SELECT l FROM LaboratoryAnalysisResult l WHERE l.patient.cnp = :cnp")
    List<LaboratoryAnalysisResult> findAllByPatient(Long cnp);
}
