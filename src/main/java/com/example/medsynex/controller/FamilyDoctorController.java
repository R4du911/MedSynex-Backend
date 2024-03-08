package com.example.medsynex.controller;

import com.example.medsynex.model.Dispensary;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.service.FamilyDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/family-doctor")
public class FamilyDoctorController {

    @Autowired
    private FamilyDoctorService familyDoctorService;

    @PostMapping("/get-by-dispensary")
    public ResponseEntity<List<FamilyDoctor>> getAllFamilyDoctorsFromAGivenDispensary(@RequestBody Dispensary dispensary){
        return new ResponseEntity<>(familyDoctorService.getAllFamilyDoctorsFromAGivenDispensary(dispensary), HttpStatus.OK);
    }
}
