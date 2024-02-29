package com.example.medsynex.repository;

import com.example.medsynex.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    @Query("SELECT h FROM Hospital h")
    List<Hospital> findAll();
}
