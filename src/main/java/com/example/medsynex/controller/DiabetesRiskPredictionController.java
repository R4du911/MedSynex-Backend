package com.example.medsynex.controller;

import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.DiabetesRiskPredictionSavedData;
import com.example.medsynex.service.DiabetesRiskPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diabetes-prediction")
public class DiabetesRiskPredictionController {

    @Autowired
    private DiabetesRiskPredictionService diabetesRiskPredictionService;

    @GetMapping("/retrieve-saved-data/{cnp}")
    public ResponseEntity<DiabetesRiskPredictionSavedData> getDiabetesRiskPredictionSavedData(@PathVariable Long cnp) {
        return new ResponseEntity<>(diabetesRiskPredictionService.getDiabetesRiskPredictionSavedData(cnp), HttpStatus.OK);
    }

    @PostMapping("{cnp}")
    public ResponseEntity<Void> requestDiabetesRiskPrediction(@PathVariable Long cnp, @RequestBody DiabetesRiskPredictionRequestDTO diabetesRiskPredictionRequestDTO) throws BusinessException {
        diabetesRiskPredictionService.requestDiabetesRiskPrediction(cnp, diabetesRiskPredictionRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
