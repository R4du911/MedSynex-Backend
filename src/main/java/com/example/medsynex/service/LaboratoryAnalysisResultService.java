package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.model.dto.CreateLaboratoryAnalysisResultRequestDTO;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LaboratoryAnalysisResultService {

    @Autowired
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LaboratoryAnalysisResult> getAllLaboratoryAnalysisResultsByPatient(Long cnp) {
        return laboratoryAnalysisResultRepository.findAllByPatient(cnp);
    }

    public void createLaboratoryAnalysisResult(Long cnp, CreateLaboratoryAnalysisResultRequestDTO createLaboratoryAnalysisResultRequestDTO) throws BusinessException {
        Patient patient = patientRepository.findById(cnp)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_PATIENT));

        User user = userRepository.findByUsername(createLaboratoryAnalysisResultRequestDTO.getLaboratoryStaffUsername())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        LaboratoryAnalysisResult laboratoryAnalysisResultToSave = LaboratoryAnalysisResult.builder()
                .patient(patient)
                .createDate(LocalDate.now())
                .updateDate(LocalDate.now())
                .glucose(createLaboratoryAnalysisResultRequestDTO.getGlucose())
                .insulin(createLaboratoryAnalysisResultRequestDTO.getInsulin())
                .cholesterol(createLaboratoryAnalysisResultRequestDTO.getCholesterol())
                .triglyceride(createLaboratoryAnalysisResultRequestDTO.getTriglyceride())
                .haemoglobin(createLaboratoryAnalysisResultRequestDTO.getHaemoglobin())
                .leukocyteCount(createLaboratoryAnalysisResultRequestDTO.getLeukocyteCount())
                .plateletCount(createLaboratoryAnalysisResultRequestDTO.getPlateletCount())
                .totalCalcium(createLaboratoryAnalysisResultRequestDTO.getTotalCalcium())
                .totalMagnesium(createLaboratoryAnalysisResultRequestDTO.getTotalMagnesium())
                .serumIron(createLaboratoryAnalysisResultRequestDTO.getSerumIron())
                .serumSodium(createLaboratoryAnalysisResultRequestDTO.getSerumSodium())
                .serumPotassium(createLaboratoryAnalysisResultRequestDTO.getSerumPotassium())
                .laboratoryName(user.getLaboratory().getName())
                .diabetesRisk(null)
                .remarks(null)
                .build();

        laboratoryAnalysisResultRepository.save(laboratoryAnalysisResultToSave);
    }
}
