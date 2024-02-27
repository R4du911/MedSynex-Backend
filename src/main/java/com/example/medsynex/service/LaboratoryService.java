package com.example.medsynex.service;

import com.example.medsynex.model.Laboratory;
import com.example.medsynex.repository.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findAll();
    }
}
