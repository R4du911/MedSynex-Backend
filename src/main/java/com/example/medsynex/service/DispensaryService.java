package com.example.medsynex.service;

import com.example.medsynex.model.Dispensary;
import com.example.medsynex.repository.DispensaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispensaryService {

    @Autowired
    private DispensaryRepository dispensaryRepository;

    public List<Dispensary> getAllDispensaries() {
        return dispensaryRepository.findAll();
    }
}
