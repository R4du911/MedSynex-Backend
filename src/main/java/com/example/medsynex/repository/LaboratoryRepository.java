package com.example.medsynex.repository;

import com.example.medsynex.model.Laboratory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryRepository extends CrudRepository<Laboratory, Long> {
    @Query("SELECT l FROM Laboratory l")
    List<Laboratory> findAll();

}
