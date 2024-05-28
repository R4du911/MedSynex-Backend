package com.example.medsynex.service;

import com.example.medsynex.dto.diabetesRiskPrediction.DiabetesRiskPredictionRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.DiabetesRiskPredictionSavedData;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.model.Patient;
import com.example.medsynex.repository.DiabetesRiskPredictionRepository;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import com.example.medsynex.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiabetesRiskPredictionServiceTest {

    @InjectMocks
    private DiabetesRiskPredictionService diabetesRiskPredictionService;

    @Mock
    private DiabetesRiskPredictionRepository diabetesRiskPredictionRepository;

    @Mock
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void testGetDiabetesRiskPredictionSavedData() {
        Long cnp = 123L;
        DiabetesRiskPredictionSavedData data = new DiabetesRiskPredictionSavedData();
        when(diabetesRiskPredictionRepository.findByPatient(cnp)).thenReturn(data);

        DiabetesRiskPredictionSavedData result = diabetesRiskPredictionService.getDiabetesRiskPredictionSavedData(cnp);

        assertEquals(data, result);
        verify(diabetesRiskPredictionRepository).findByPatient(cnp);
    }

    @Test
    void testRequestDiabetesRiskPrediction() throws BusinessException {
        Long cnp = 123L;
        DiabetesRiskPredictionRequestDTO requestDTO = new DiabetesRiskPredictionRequestDTO(1L, 1, 130.5,
                85.2, 1.4, 70.0, 175.5, 67.4, 2,
                3, 30);

        LaboratoryAnalysisResult labResult = new LaboratoryAnalysisResult();
        when(laboratoryAnalysisResultRepository.findById(requestDTO.getLaboratoryAnalysisResultID())).thenReturn(Optional.of(labResult));

        ResponseEntity<String> responseEntity = new ResponseEntity<>("1", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        Patient patient = new Patient();
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));

        when(diabetesRiskPredictionRepository.findByPatient(cnp)).thenReturn(null);

        diabetesRiskPredictionService.requestDiabetesRiskPrediction(cnp, requestDTO);

        ArgumentCaptor<DiabetesRiskPredictionSavedData> savedDataCaptor = ArgumentCaptor.forClass(DiabetesRiskPredictionSavedData.class);
        verify(diabetesRiskPredictionRepository).save(savedDataCaptor.capture());
        DiabetesRiskPredictionSavedData savedData = savedDataCaptor.getValue();

        assertNotNull(savedData);
        assertEquals(patient, savedData.getPatient());
        assertEquals(requestDTO.getPregnancies(), savedData.getPregnancies());
        assertEquals(requestDTO.getSkinThickness(), savedData.getSkinThickness());
        assertEquals(requestDTO.getFirstDegreeDiabetesCount(), savedData.getFirstDegreeDiabetesCount());
        assertEquals(requestDTO.getSecondDegreeDiabetesCount(), savedData.getSecondDegreeDiabetesCount());
        assertEquals(requestDTO.getAge(), savedData.getAge());

        ArgumentCaptor<LaboratoryAnalysisResult> labResultDataCaptor = ArgumentCaptor.forClass(LaboratoryAnalysisResult.class);
        verify(laboratoryAnalysisResultRepository).save(labResultDataCaptor.capture());
        LaboratoryAnalysisResult savedLabResult = labResultDataCaptor.getValue();

        assertNotNull(savedLabResult);
        assertEquals(savedLabResult.getDiabetesRisk(), true);
    }

    @Test
    void testRequestDiabetesRiskPrediction_InvalidLabResult() {
        Long cnp = 123L;
        DiabetesRiskPredictionRequestDTO requestDTO = new DiabetesRiskPredictionRequestDTO(1L, 1, 130.5,
                85.2, 1.4, 70.0, 175.5, 67.4, 2,
                3, 30);
        requestDTO.setLaboratoryAnalysisResultID(1L);

        when(laboratoryAnalysisResultRepository.findById(requestDTO.getLaboratoryAnalysisResultID())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                diabetesRiskPredictionService.requestDiabetesRiskPrediction(cnp, requestDTO));

        assertEquals(BusinessExceptionCode.INVALID_DATA, exception.getBusinessExceptionCode());
    }

    @Test
    void testRequestDiabetesRiskPrediction_InvalidPatient() {
        Long cnp = 123L;
        DiabetesRiskPredictionRequestDTO requestDTO = new DiabetesRiskPredictionRequestDTO(1L, 1, 130.5,
                85.2, 1.4, 70.0, 175.5, 67.4, 2,
                3, 30);
        requestDTO.setLaboratoryAnalysisResultID(1L);

        LaboratoryAnalysisResult labResult = new LaboratoryAnalysisResult();
        when(laboratoryAnalysisResultRepository.findById(requestDTO.getLaboratoryAnalysisResultID())).thenReturn(Optional.of(labResult));

        ResponseEntity<String> responseEntity = new ResponseEntity<>("1", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                diabetesRiskPredictionService.requestDiabetesRiskPrediction(cnp, requestDTO));

        assertEquals(BusinessExceptionCode.INVALID_PATIENT, exception.getBusinessExceptionCode());
    }
}
