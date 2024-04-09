package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/family-doctor/{username}")
    public ResponseEntity<FamilyDoctor> getRegisteredFamilyDoctorOfGivenPatient(@PathVariable String username) throws BusinessException {
        return new ResponseEntity<>(patientService.getRegisteredFamilyDoctorOfGivenPatient(username), HttpStatus.OK);
    }
}
