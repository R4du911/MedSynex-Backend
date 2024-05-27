package com.example.medsynex.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.medsynex.dto.consultation.CreateConsultationRequestDTO;
import com.example.medsynex.dto.consultation.UpdateConsultationRequestDTO;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.Consultation;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.ConsultationRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConsultationService consultationService;

    private Patient patient;
    private User user;
    private Consultation consultation;
    private CreateConsultationRequestDTO createRequestDTO;
    private UpdateConsultationRequestDTO updateRequestDTO;

    @BeforeEach
    public void setUp() {
        patient = new Patient();
        patient.setCnp(5020911376892L);

        user = new User();
        user.setUsername("doctorUsername");
        user.setFirstName("Doctor");
        user.setLastName("Username");

        consultation = Consultation.builder()
                .id(1L)
                .patient(patient)
                .createDate(LocalDate.now())
                .updateDate(LocalDate.now())
                .doctorFirstName(user.getFirstName())
                .doctorLastName(user.getLastName())
                .doctorUsername(user.getUsername())
                .diagnosis("Initial Diagnosis")
                .remarks("Initial Remarks")
                .medications("Medication1, Medication2")
                .build();

        createRequestDTO = new CreateConsultationRequestDTO(
                "doctorUsername",
                "Diagnosis Example",
                "Remarks Example",
                "Medication1, Medication2"
        );

        updateRequestDTO = new UpdateConsultationRequestDTO(
                1L,
                "Updated Diagnosis",
                "Updated Remarks",
                "Updated Medication1, Updated Medication2"
        );
    }

    @Test
    void testGetAllConsultationsByPatient() {
        Long cnp = 1L;
        List<Consultation> consultations = Collections.singletonList(consultation);
        when(consultationRepository.findAllByPatient(cnp)).thenReturn(consultations);

        List<Consultation> result = consultationService.getAllConsultationsByPatient(cnp);

        assertEquals(consultations, result);
    }

    @Test
    void testCreateConsultation_Success() throws BusinessException {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(userRepository.findByUsername("doctorUsername")).thenReturn(Optional.of(user));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        consultationService.createConsultation(cnp, createRequestDTO);

        verify(consultationRepository, times(1)).save(any(Consultation.class));
    }

    @Test
    void testCreateConsultation_InvalidPatient() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                consultationService.createConsultation(cnp, createRequestDTO));

        assertEquals(BusinessExceptionCode.INVALID_PATIENT, exception.getBusinessExceptionCode());
    }

    @Test
    void testCreateConsultation_UserDoesNotExist() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(userRepository.findByUsername("doctorUsername")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                consultationService.createConsultation(cnp, createRequestDTO));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testUpdateConsultation_Success() throws BusinessException {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(consultationRepository.findById(updateRequestDTO.getId())).thenReturn(Optional.of(consultation));
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        consultationService.updateConsultation(cnp, updateRequestDTO);

        verify(consultationRepository, times(1)).save(any(Consultation.class));
        assertEquals("Updated Diagnosis", consultation.getDiagnosis());
        assertEquals("Updated Remarks", consultation.getRemarks());
        assertEquals("Updated Medication1, Updated Medication2", consultation.getMedications());
    }

    @Test
    void testUpdateConsultation_InvalidPatient() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                consultationService.updateConsultation(cnp, updateRequestDTO));

        assertEquals(BusinessExceptionCode.INVALID_PATIENT, exception.getBusinessExceptionCode());
    }

    @Test
    void testUpdateConsultation_InvalidData() {
        Long cnp = 1L;
        when(patientRepository.findById(cnp)).thenReturn(Optional.of(patient));
        when(consultationRepository.findById(updateRequestDTO.getId())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                consultationService.updateConsultation(cnp, updateRequestDTO));

        assertEquals(BusinessExceptionCode.INVALID_DATA, exception.getBusinessExceptionCode());
    }
}