package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.service.FamilyDoctorRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family-doctor-request")
public class FamilyDoctorRequestController {

    @Autowired
    private FamilyDoctorRequestService familyDoctorRequestService;

    @GetMapping
    public ResponseEntity<List<FamilyDoctorRequest>> getAllFamilyDoctorRequest() {
        return new ResponseEntity<>(familyDoctorRequestService.getAllFamilyDoctorRequest(), HttpStatus.OK);
    }

    @PostMapping("/make/{username}")
    public ResponseEntity<String> makeAFamilyDoctorRequest(@PathVariable String username, @RequestBody FamilyDoctor selectedFamilyDoctor) throws BusinessException {
        familyDoctorRequestService.makeAFamilyDoctorRequest(username, selectedFamilyDoctor);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
