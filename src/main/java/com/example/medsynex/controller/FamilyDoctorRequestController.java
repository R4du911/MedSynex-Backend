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

    @GetMapping("/family-doctor/{username}")
    public ResponseEntity<List<FamilyDoctorRequest>> getAllFamilyDoctorRequestForAGivenFamilyDoctor(@PathVariable String username) throws BusinessException {
        return new ResponseEntity<>(familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenFamilyDoctor(username), HttpStatus.OK);
    }

    @GetMapping("/patient/{username}")
    public ResponseEntity<List<FamilyDoctorRequest>> getAllFamilyDoctorRequestForAGivenPatient(@PathVariable String username) throws BusinessException {
        return new ResponseEntity<>(familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenPatient(username), HttpStatus.OK);
    }

    @PostMapping("/make/{username}")
    public ResponseEntity<Void> makeAFamilyDoctorRequest(@PathVariable String username, @RequestBody FamilyDoctor selectedFamilyDoctor) throws BusinessException {
        familyDoctorRequestService.makeAFamilyDoctorRequest(username, selectedFamilyDoctor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptRequest(@RequestBody FamilyDoctorRequest familyDoctorRequest) {
        familyDoctorRequestService.acceptRequest(familyDoctorRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> declineRequest(@RequestBody FamilyDoctorRequest familyDoctorRequest) {
        familyDoctorRequestService.declineRequest(familyDoctorRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
