package com.example.medsynex.controller;

import com.example.medsynex.model.Dispensary;
import com.example.medsynex.service.DispensaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dispensary")
public class DispensaryController {

    @Autowired
    private DispensaryService dispensaryService;

    @GetMapping
    public ResponseEntity<List<Dispensary>> getAllDispensaries() {
        return new ResponseEntity<>(dispensaryService.getAllDispensaries(), HttpStatus.OK);
    }

}
