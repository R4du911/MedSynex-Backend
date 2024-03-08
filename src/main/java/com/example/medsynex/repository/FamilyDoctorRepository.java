package com.example.medsynex.repository;

import com.example.medsynex.model.FamilyDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyDoctorRepository extends JpaRepository<FamilyDoctor, Long> {
    List<FamilyDoctor> findFamilyDoctorByDispensary_Id(Long id);
}
