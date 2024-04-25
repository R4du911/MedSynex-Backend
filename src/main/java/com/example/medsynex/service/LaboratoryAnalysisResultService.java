package com.example.medsynex.service;

import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratoryAnalysisResultService {

    @Autowired
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    public List<LaboratoryAnalysisResult> getAllLaboratoryAnalysisResultsByPatient(Long cnp) {
        return laboratoryAnalysisResultRepository.findAllByPatient(cnp);
    }
}
