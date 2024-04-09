package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.Dispensary;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.Patient;
import com.example.medsynex.service.FamilyDoctorService;
import com.example.medsynex.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family-doctor")
public class FamilyDoctorController {

    @Autowired
    private FamilyDoctorService familyDoctorService;

    @Autowired
    private PatientService patientService;

    @PostMapping("/get-by-dispensary")
    public ResponseEntity<List<FamilyDoctor>> getAllFamilyDoctorsFromAGivenDispensary(@RequestBody Dispensary dispensary){
        return new ResponseEntity<>(familyDoctorService.getAllFamilyDoctorsFromAGivenDispensary(dispensary), HttpStatus.OK);
    }

    @GetMapping("/patients/{username}")
    public ResponseEntity<List<Patient>> getAllPatientsRegisteredAtAGivenFamilyDoctor(@PathVariable String username) throws BusinessException {
        return new ResponseEntity<>(patientService.getAllPatientsRegisteredAtAGivenFamilyDoctor(username), HttpStatus.OK);
    }
}
