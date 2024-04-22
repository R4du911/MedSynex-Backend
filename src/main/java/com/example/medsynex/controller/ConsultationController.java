package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.Consultation;
import com.example.medsynex.model.dto.CreateConsultationRequestDTO;
import com.example.medsynex.model.dto.UpdateConsultationRequestDTO;
import com.example.medsynex.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/{cnp}")
    public ResponseEntity<List<Consultation>> getAllConsultationsByPatient(@PathVariable Long cnp) {
        return new ResponseEntity<>(consultationService.getAllConsultationsByPatient(cnp), HttpStatus.OK);
    }

    @PostMapping("/{cnp}")
    public ResponseEntity<Void> createConsultation(@PathVariable Long cnp, @RequestBody CreateConsultationRequestDTO createConsultationRequestDTO) throws BusinessException {
        consultationService.createConsultation(cnp, createConsultationRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{cnp}")
    public ResponseEntity<Void> updateConsultation(@PathVariable Long cnp, @RequestBody UpdateConsultationRequestDTO updateConsultationRequestDTO) throws BusinessException {
        consultationService.updateConsultation(cnp, updateConsultationRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
