package com.example.medsynex.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.model.compositeKeys.FamilyDoctorRequestPK;
import com.example.medsynex.repository.FamilyDoctorRepository;
import com.example.medsynex.repository.FamilyDoctorRequestRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FamilyDoctorRequestServiceTest {

    @Mock
    private FamilyDoctorRequestRepository familyDoctorRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private FamilyDoctorRepository familyDoctorRepository;

    @InjectMocks
    private FamilyDoctorRequestService familyDoctorRequestService;

    private User userRegisteredAsPatient;

    private User userRegisteredAsFamilyDoctor;
    private Patient patient;
    private FamilyDoctor familyDoctor;
    private FamilyDoctorRequest familyDoctorRequest;

    @BeforeEach
    public void setUp() {
        patient = new Patient();
        patient.setCnp(1L);

        familyDoctor = new FamilyDoctor();
        familyDoctor.setId(1L);
        familyDoctor.setNrPatients(10);

        userRegisteredAsPatient = new User();
        userRegisteredAsPatient.setUsername("testUser");
        userRegisteredAsPatient.setPatient(patient);

        userRegisteredAsFamilyDoctor = new User();
        userRegisteredAsFamilyDoctor.setUsername("testFamilyDoctor");
        userRegisteredAsFamilyDoctor.setFamilyDoctor(familyDoctor);

        familyDoctorRequest = FamilyDoctorRequest.builder()
                .patient(patient)
                .familyDoctor(familyDoctor)
                .build();
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenFamilyDoctor_Success() throws BusinessException {
        when(userRepository.findByUsername("testFamilyDoctor")).thenReturn(Optional.of(userRegisteredAsFamilyDoctor));
        when(familyDoctorRequestRepository.findAllByFamilyDoctor(familyDoctor)).thenReturn(Collections.singletonList(familyDoctorRequest));

        List<FamilyDoctorRequest> result = familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenFamilyDoctor("testFamilyDoctor");

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenFamilyDoctor_InvalidUser() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenFamilyDoctor("invalidUser"));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenFamilyDoctor_UserIsNotFamilyDoctor() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userRegisteredAsPatient));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenFamilyDoctor("testUser"));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenPatient_Success() throws BusinessException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userRegisteredAsPatient));
        when(familyDoctorRequestRepository.findAllByPatient(patient)).thenReturn(Collections.singletonList(familyDoctorRequest));

        List<FamilyDoctorRequest> result = familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenPatient("testUser");

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenPatient_InvalidUser() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenPatient("invalidUser"));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetAllFamilyDoctorRequestForAGivenPatient_UserIsNotPatient() {
        when(userRepository.findByUsername("testFamilyDoctor")).thenReturn(Optional.of(userRegisteredAsFamilyDoctor));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.getAllFamilyDoctorRequestForAGivenPatient("testFamilyDoctor"));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testMakeAFamilyDoctorRequest_Success() throws BusinessException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userRegisteredAsPatient));
        when(familyDoctorRequestRepository.findById(new FamilyDoctorRequestPK(patient.getCnp(), familyDoctor.getId())))
                .thenReturn(Optional.empty());

        familyDoctorRequestService.makeAFamilyDoctorRequest("testUser", familyDoctor);

        verify(familyDoctorRequestRepository, times(1)).save(any(FamilyDoctorRequest.class));
    }

    @Test
    void testMakeAFamilyDoctorRequest_FamilyDoctorHasMaxPatients() {
        familyDoctor.setNrPatients(20);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.makeAFamilyDoctorRequest("testUser", familyDoctor));

        assertEquals(BusinessExceptionCode.FAMILY_DOCTOR_HAS_MAX_PATIENTS, exception.getBusinessExceptionCode());
    }

    @Test
    void testMakeAFamilyDoctorRequest_InvalidUser() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.makeAFamilyDoctorRequest("invalidUser", familyDoctor));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testMakeAFamilyDoctorRequest_UserIsNotPatient() {
        when(userRepository.findByUsername("testFamilyDoctor")).thenReturn(Optional.of(userRegisteredAsFamilyDoctor));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.makeAFamilyDoctorRequest("testFamilyDoctor", familyDoctor));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testMakeAFamilyDoctorRequest_RequestAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userRegisteredAsPatient));
        when(familyDoctorRequestRepository.findById(new FamilyDoctorRequestPK(patient.getCnp(), familyDoctor.getId())))
                .thenReturn(Optional.of(familyDoctorRequest));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                familyDoctorRequestService.makeAFamilyDoctorRequest("testUser", familyDoctor));

        assertEquals(BusinessExceptionCode.FAMILY_DOCTOR_REQUEST_ALREADY_EXISTS, exception.getBusinessExceptionCode());
    }

    @Test
    void testAcceptRequest_Success() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(familyDoctorRepository.save(any(FamilyDoctor.class))).thenReturn(familyDoctor);

        familyDoctorRequestService.acceptRequest(familyDoctorRequest);

        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(familyDoctorRepository, times(1)).save(any(FamilyDoctor.class));
        verify(familyDoctorRequestRepository, times(1)).deleteFamilyDoctorRequestByPatient(any(Patient.class));
    }

    @Test
    void testAcceptRequest_SuccessAndFamilyDoctorHasMaxPatients() {
        familyDoctor.setNrPatients(19);

        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(familyDoctorRepository.save(any(FamilyDoctor.class))).thenReturn(familyDoctor);

        familyDoctorRequestService.acceptRequest(familyDoctorRequest);

        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(familyDoctorRepository, times(1)).save(any(FamilyDoctor.class));
        verify(familyDoctorRequestRepository, times(1)).deleteFamilyDoctorRequestByPatient(any(Patient.class));
        verify(familyDoctorRequestRepository, times(1)).deleteFamilyDoctorRequestByFamilyDoctor(any(FamilyDoctor.class));
    }

    @Test
    void testDeclineRequest_Success() {
        familyDoctorRequestService.declineRequest(familyDoctorRequest);

        verify(familyDoctorRequestRepository, times(1)).delete(familyDoctorRequest);
    }
}