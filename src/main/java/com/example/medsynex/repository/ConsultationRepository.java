package com.example.medsynex.repository;

import com.example.medsynex.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    @Query("SELECT c FROM Consultation c WHERE c.patient.cnp = :cnp")
    List<Consultation> findAllByPatient(Long cnp);
}
