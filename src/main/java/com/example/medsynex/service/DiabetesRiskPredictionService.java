package com.example.medsynex.service;

import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionAIRequestDTO;
import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.DiabetesRiskPredictionSavedData;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.model.Patient;
import com.example.medsynex.repository.DiabetesRiskPredictionRepository;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import com.example.medsynex.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class DiabetesRiskPredictionService {

    @Autowired
    private DiabetesRiskPredictionRepository diabetesRiskPredictionRepository;

    @Autowired
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    RestTemplate restTemplate;

    public DiabetesRiskPredictionSavedData getDiabetesRiskPredictionSavedData(Long cnp) {
        return diabetesRiskPredictionRepository.findByPatient(cnp);
    }

    public void requestDiabetesRiskPrediction(Long cnp, DiabetesRiskPredictionRequestDTO diabetesRiskPredictionRequestDTO) throws BusinessException {
        LaboratoryAnalysisResult laboratoryAnalysisResultFromDB = laboratoryAnalysisResultRepository.findById(diabetesRiskPredictionRequestDTO.getLaboratoryAnalysisResultID())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_DATA));

        Double bmi = diabetesRiskPredictionRequestDTO.getWeight() /
                ((diabetesRiskPredictionRequestDTO.getHeight() / 100) * (diabetesRiskPredictionRequestDTO.getHeight() / 100));

        Double diabetesPedigreeFunction = diabetesRiskPredictionRequestDTO.getFirstDegreeDiabetesCount() * 0.5 +
                diabetesRiskPredictionRequestDTO.getSecondDegreeDiabetesCount() * 0.3;

        DiabetesRiskPredictionAIRequestDTO diabetesRiskPredictionAIRequestDTO = DiabetesRiskPredictionAIRequestDTO.builder()
                .pregnancies(diabetesRiskPredictionRequestDTO.getPregnancies())
                .glucose(diabetesRiskPredictionRequestDTO.getGlucose())
                .bloodPressure(diabetesRiskPredictionRequestDTO.getBloodPressure())
                .skinThickness(scaleSkinThickness(diabetesRiskPredictionRequestDTO.getSkinThickness()))
                .insulin(diabetesRiskPredictionRequestDTO.getInsulin())
                .bmi(bmi)
                .diabetesPedigreeFunction(diabetesPedigreeFunction)
                .age(diabetesRiskPredictionRequestDTO.getAge())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<DiabetesRiskPredictionAIRequestDTO> entity = new HttpEntity<>(diabetesRiskPredictionAIRequestDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:5000/diabetes-prediction",
                HttpMethod.POST,
                entity,
                String.class
        );

        laboratoryAnalysisResultFromDB.setUpdateDate(LocalDate.now());
        laboratoryAnalysisResultFromDB.setDiabetesRisk(parseBooleanFromResponse(response.getBody()));

        DiabetesRiskPredictionSavedData diabetesRiskPredictionSavedData = diabetesRiskPredictionRepository.findByPatient(cnp);

        if (diabetesRiskPredictionSavedData == null) {
            diabetesRiskPredictionSavedData = new DiabetesRiskPredictionSavedData();

            Patient patientFromDB = patientRepository.findById(cnp)
                    .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_PATIENT));

            diabetesRiskPredictionSavedData.setPatient(patientFromDB);
        }

        diabetesRiskPredictionSavedData.setPregnancies(diabetesRiskPredictionRequestDTO.getPregnancies());
        diabetesRiskPredictionSavedData.setSkinThickness(diabetesRiskPredictionRequestDTO.getSkinThickness());
        diabetesRiskPredictionSavedData.setFirstDegreeDiabetesCount(diabetesRiskPredictionRequestDTO.getFirstDegreeDiabetesCount());
        diabetesRiskPredictionSavedData.setSecondDegreeDiabetesCount(diabetesRiskPredictionRequestDTO.getSecondDegreeDiabetesCount());
        diabetesRiskPredictionSavedData.setAge(diabetesRiskPredictionRequestDTO.getAge());

        laboratoryAnalysisResultRepository.save(laboratoryAnalysisResultFromDB);
        diabetesRiskPredictionRepository.save(diabetesRiskPredictionSavedData);
    }

    private double scaleSkinThickness(double value) {
        return ((value - 0.3) / (2.6 - 0.3)) * (70.0 - 1.0) + 1.0;
    }

    private boolean parseBooleanFromResponse(String response) {
        if (response != null) {
            String trimmedResponse = response.trim();
            return trimmedResponse.startsWith("1");
        }

        return false;
    }
}
