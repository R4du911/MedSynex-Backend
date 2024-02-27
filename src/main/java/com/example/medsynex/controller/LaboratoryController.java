package com.example.medsynex.controller;

import com.example.medsynex.model.Laboratory;
import com.example.medsynex.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("laboratory")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    @GetMapping
    public ResponseEntity<List<Laboratory>> getAllLaboratories() {
        return new ResponseEntity<>(this.laboratoryService.getAllLaboratories(), HttpStatus.OK);
    }


}
