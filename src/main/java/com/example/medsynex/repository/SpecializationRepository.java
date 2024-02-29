package com.example.medsynex.repository;

import com.example.medsynex.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    @Query("SELECT s FROM Specialization s")
    List<Specialization> findAll();
}
