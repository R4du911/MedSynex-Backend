package com.example.medsynex.repository;

import com.example.medsynex.model.Dispensary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispensaryRepository extends JpaRepository<Dispensary, Long> {
    @Query("SELECT d FROM Dispensary d")
    List<Dispensary> findAll();
}
