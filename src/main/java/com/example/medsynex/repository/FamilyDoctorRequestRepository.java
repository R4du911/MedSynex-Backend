package com.example.medsynex.repository;

import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.model.compositeKeys.FamilyDoctorRequestPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyDoctorRequestRepository extends JpaRepository<FamilyDoctorRequest, FamilyDoctorRequestPK> {
    @Query("SELECT f FROM FamilyDoctorRequest f")
    List<FamilyDoctorRequest> findAll();
}
