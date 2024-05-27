package com.example.medsynex.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.medsynex.model.Laboratory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.medsynex.dto.laboratoryAnalysisResult.CreateLaboratoryAnalysisResultRequestDTO;
import com.example.medsynex.dto.laboratoryAnalysisResult.UpdateRemarksLaboratoryAnalysisResultRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.LaboratoryAnalysisResult;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.LaboratoryAnalysisResultRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LaboratoryAnalysisResultServiceTest {

    @Mock
    private LaboratoryAnalysisResultRepository laboratoryAnalysisResultRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LaboratoryAnalysisResultService laboratoryAnalysisResultService;

    private Patient patient;
    private User user;
    private LaboratoryAnalysisResult laboratoryAnalysisResult;
    private CreateLaboratoryAnalysisResultRequestDTO createRequestDTO;
    private UpdateRemarksLaboratoryAnalysisResultRequestDTO updateRequestDTO;

    @BeforeEach
    public void setUp() {
        patient = new Patient();
        patient.setCnp(5020911376892L);

        user = new User();
        user.setUsername("labStaff");
        user.setLaboratory(new Laboratory());
        user.getLaboratory().setName("LabName");

        laboratoryAnalysisResult = LaboratoryAnalysisResult.builder()
                .id(1L)
                .patient(patient)
                .createDate(LocalDate.now())
                .updateDate(LocalDate.now())
                .remarks("Initial remarks")
                .build();

        createRequestDTO = new CreateLaboratoryAnalysisResultRequestDTO(
                98.0, 100.0, 190.0, 150.0, 13.5, 7.0,
                250.0, 9.5, 2.1, 60.0, 140.0, 4.0,
                "labStaff"
        );

        updateRequestDTO = new UpdateRemarksLaboratoryAnalysisResultRequestDTO(1L,"Updated remarks");
    }

    @Test
    void testGetAllLaboratoryAnalysisResultsByPatient() {
        Long cnp = 1L;
        List<LaboratoryAnalysisResult> results = Collections.singletonList(laboratoryAnalysisResult);
        when(laboratoryAnalysisResultRepository.findAllByPatient(cnp)).thenReturn(results);

        List<LaboratoryAnalysisResult> result = laboratoryAnalysisResultService.getAllLaboratoryAnalysisResultsByPatient(cnp);

        assertEquals(results, result);
    }

    @Test
    void testCreateLaboratoryAnalysisResult_Success() throws BusinessException {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(userRepository.findByUsername("labStaff")).thenReturn(Optional.of(user));
        when(laboratoryAnalysisResultRepository.save(any(LaboratoryAnalysisResult.class))).thenReturn(laboratoryAnalysisResult);

        laboratoryAnalysisResultService.createLaboratoryAnalysisResult(cnp, createRequestDTO);

        verify(laboratoryAnalysisResultRepository, times(1)).save(any(LaboratoryAnalysisResult.class));
    }

    @Test
    void testCreateLaboratoryAnalysisResult_InvalidPatient() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            laboratoryAnalysisResultService.createLaboratoryAnalysisResult(cnp, createRequestDTO);
        });

        assertEquals(BusinessExceptionCode.INVALID_PATIENT, exception.getBusinessExceptionCode());
    }

    @Test
    void testCreateLaboratoryAnalysisResult_UserIsNotLabStaff() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(userRepository.findByUsername("labStaff")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            laboratoryAnalysisResultService.createLaboratoryAnalysisResult(cnp, createRequestDTO);
        });

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testUpdateRemarksForLaboratoryAnalysisResult_Success() throws BusinessException {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(laboratoryAnalysisResultRepository.findById(updateRequestDTO.getId())).thenReturn(Optional.of(laboratoryAnalysisResult));
        when(laboratoryAnalysisResultRepository.save(any(LaboratoryAnalysisResult.class))).thenReturn(laboratoryAnalysisResult);

        laboratoryAnalysisResultService.updateRemarksForLaboratoryAnalysisResult(cnp, updateRequestDTO);

        verify(laboratoryAnalysisResultRepository, times(1)).save(any(LaboratoryAnalysisResult.class));
        assertEquals("Updated remarks", laboratoryAnalysisResult.getRemarks());
    }

    @Test
    void testUpdateRemarksForLaboratoryAnalysisResult_InvalidPatient() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            laboratoryAnalysisResultService.updateRemarksForLaboratoryAnalysisResult(cnp, updateRequestDTO);
        });

        assertEquals(BusinessExceptionCode.INVALID_PATIENT, exception.getBusinessExceptionCode());
    }

    @Test
    void testUpdateRemarksForLaboratoryAnalysisResult_InvalidData() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(laboratoryAnalysisResultRepository.findById(updateRequestDTO.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            laboratoryAnalysisResultService.updateRemarksForLaboratoryAnalysisResult(cnp, updateRequestDTO);
        });

        assertEquals(BusinessExceptionCode.INVALID_DATA, exception.getBusinessExceptionCode());
    }
}