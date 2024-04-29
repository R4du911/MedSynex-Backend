package com.example.medsynex.controller;

import com.example.medsynex.dto.laboratoryAnalysisResult.UpdateRemarksLaboratoryAnalysisResultRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.dto.laboratoryAnalysisResult.CreateLaboratoryAnalysisResultRequestDTO;
import com.example.medsynex.service.LaboratoryAnalysisResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laboratory-analysis-result")
public class LaboratoryAnalysisResultController {

    @Autowired
    private LaboratoryAnalysisResultService laboratoryAnalysisResultService;

    @GetMapping("/{cnp}")
    public ResponseEntity<List<LaboratoryAnalysisResult>> getAllLaboratoryAnalysisResultsByPatient(@PathVariable Long cnp) {
        return new ResponseEntity<>(laboratoryAnalysisResultService.getAllLaboratoryAnalysisResultsByPatient(cnp), HttpStatus.OK);
    }

    @PostMapping("{cnp}")
    public ResponseEntity<Void> createLaboratoryAnalysisResult(@PathVariable Long cnp, @RequestBody CreateLaboratoryAnalysisResultRequestDTO createLaboratoryAnalysisResultRequestDTO) throws BusinessException {
        laboratoryAnalysisResultService.createLaboratoryAnalysisResult(cnp, createLaboratoryAnalysisResultRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{cnp}/remarks")
    public ResponseEntity<Void> updateRemarksForLaboratoryAnalysisResult(@PathVariable Long cnp, @RequestBody UpdateRemarksLaboratoryAnalysisResultRequestDTO updateRemarksLaboratoryAnalysisResultRequestDTO) throws BusinessException {
        laboratoryAnalysisResultService.updateRemarksForLaboratoryAnalysisResult(cnp, updateRemarksLaboratoryAnalysisResultRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
