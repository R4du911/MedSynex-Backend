package com.example.medsynex.repository;

import com.example.medsynex.model.FamilyDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyDoctorRepository extends JpaRepository<FamilyDoctor, Long> {
}
