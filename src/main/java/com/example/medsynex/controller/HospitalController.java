package com.example.medsynex.controller;

import com.example.medsynex.model.Hospital;
import com.example.medsynex.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        return new ResponseEntity<>(hospitalService.getAllHospitals(), HttpStatus.OK);
    }

}
