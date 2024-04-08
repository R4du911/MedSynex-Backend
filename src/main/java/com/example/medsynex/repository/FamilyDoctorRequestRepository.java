package com.example.medsynex.repository;

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
    void deleteFamilyDoctorRequestByPatient(Patient patient);
    void deleteFamilyDoctorRequestByFamilyDoctor(FamilyDoctor familyDoctor);
}
