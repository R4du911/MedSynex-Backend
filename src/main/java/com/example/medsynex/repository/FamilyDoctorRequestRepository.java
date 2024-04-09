package com.example.medsynex.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.compositeKeys.FamilyDoctorRequestPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyDoctorRequestRepository extends JpaRepository<FamilyDoctorRequest, FamilyDoctorRequestPK> {
    List<FamilyDoctorRequest> findAllByFamilyDoctor(FamilyDoctor familyDoctor);
    List<FamilyDoctorRequest> findAllByPatient(Patient patient);
    @Transactional
    @Modifying
    @Query("DELETE FROM FamilyDoctorRequest fdr WHERE fdr.patient = :patient")
    void deleteFamilyDoctorRequestByPatient(@Param("patient") Patient patient);

    @Transactional
    @Modifying
    @Query("DELETE FROM FamilyDoctorRequest fdr WHERE fdr.familyDoctor = :familyDoctor")
    void deleteFamilyDoctorRequestByFamilyDoctor(@Param("familyDoctor") FamilyDoctor familyDoctor);
}
