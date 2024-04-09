package com.example.medsynex.repository;

import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p WHERE p.familyDoctor = :familyDoctor")
    List<Patient> findAllByFamilyDoctor(FamilyDoctor familyDoctor);
}
