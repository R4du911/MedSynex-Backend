package com.example.medsynex.service;

import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionAIRequestDTO;
import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionRequestDTO;
import com.example.medsynex.dto.diabetesRiskPrediction.ESkinThicknessCategory;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.DiabetesRiskPredictionSavedData;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.repository.DiabetesRiskPredictionRepository;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiabetesRiskPredictionService {

    @Autowired
    private DiabetesRiskPredictionRepository diabetesRiskPredictionRepository;

    @Autowired
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    @Autowired
    RestTemplate restTemplate;

    public DiabetesRiskPredictionSavedData getDiabetesRiskPredictionSavedData(Long cnp) {
        return diabetesRiskPredictionRepository.findByPatient(cnp);
    }

    public void requestDiabetesRiskPrediction(Long cnp, DiabetesRiskPredictionRequestDTO diabetesRiskPredictionRequestDTO) throws BusinessException {
        LaboratoryAnalysisResult laboratoryAnalysisResultFromDB = laboratoryAnalysisResultRepository.findById(diabetesRiskPredictionRequestDTO.getLaboratoryAnalysisResultID())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_DATA));

        Double bmi = diabetesRiskPredictionRequestDTO.getWeight() /
                (diabetesRiskPredictionRequestDTO.getHeight() * diabetesRiskPredictionRequestDTO.getHeight());

        Double diabetesPedigreeFunction = diabetesRiskPredictionRequestDTO.getFirstDegreeDiabetesCount() * 0.5 +
                diabetesRiskPredictionRequestDTO.getSecondDegreeDiabetesCount() * 0.3;

        DiabetesRiskPredictionAIRequestDTO diabetesRiskPredictionAIRequestDTO = DiabetesRiskPredictionAIRequestDTO.builder()
                .Pregnancies(diabetesRiskPredictionRequestDTO.getPregnancies())
                .Glucose(diabetesRiskPredictionRequestDTO.getGlucose())
                .BloodPressure(diabetesRiskPredictionRequestDTO.getBloodPressure())
                .SkinThickness(calculateSkinThickness(diabetesRiskPredictionRequestDTO.getSkinThicknessCategory()))
                .Insulin(diabetesRiskPredictionRequestDTO.getInsulin())
                .BMI(bmi)
                .DiabetesPedigreeFunction(diabetesPedigreeFunction)
                .Age(diabetesRiskPredictionRequestDTO.getAge())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<DiabetesRiskPredictionAIRequestDTO> entity = new HttpEntity<>(diabetesRiskPredictionAIRequestDTO, headers);

        String diabetesRiskPredictionResult = restTemplate.exchange("http://localhost:5000/diabetes-prediction", HttpMethod.POST, entity, String.class).getBody();

        laboratoryAnalysisResultFromDB.setUpdateDate(LocalDate.now());
        laboratoryAnalysisResultFromDB.setDiabetesRisk(Boolean.parseBoolean(diabetesRiskPredictionResult));

        DiabetesRiskPredictionSavedData diabetesRiskPredictionSavedData = diabetesRiskPredictionRepository.findByPatient(cnp);

        if (diabetesRiskPredictionSavedData == null) {
            diabetesRiskPredictionSavedData = new DiabetesRiskPredictionSavedData();
        }

        diabetesRiskPredictionSavedData.setPregnancies(diabetesRiskPredictionRequestDTO.getPregnancies());
        diabetesRiskPredictionSavedData.setSkinThicknessCategory(diabetesRiskPredictionRequestDTO.getSkinThicknessCategory());
        diabetesRiskPredictionSavedData.setFirstDegreeDiabetesCount(diabetesRiskPredictionRequestDTO.getFirstDegreeDiabetesCount());
        diabetesRiskPredictionSavedData.setSecondDegreeDiabetesCount(diabetesRiskPredictionRequestDTO.getSecondDegreeDiabetesCount());
        diabetesRiskPredictionSavedData.setAge(diabetesRiskPredictionRequestDTO.getAge());

        laboratoryAnalysisResultRepository.save(laboratoryAnalysisResultFromDB);
        diabetesRiskPredictionRepository.save(diabetesRiskPredictionSavedData);
    }

    private double calculateSkinThickness(ESkinThicknessCategory skinThicknessCategory) {
        if (skinThicknessCategory == ESkinThicknessCategory.THIN)
            return scaleSkinThickness(1.2);
        if (skinThicknessCategory == ESkinThicknessCategory.AVERAGE)
            return scaleSkinThickness(2.0);
        if (skinThicknessCategory == ESkinThicknessCategory.THICK)
            return scaleSkinThickness(2.5);

        return 0;
    }

    private double scaleSkinThickness(double value) {
        return ((value - 0.3) / (2.6 - 0.3)) * (70.0 - 1.0) + 1.0;
    }
}
