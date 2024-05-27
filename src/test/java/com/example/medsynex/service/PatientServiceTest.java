package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void testGetRegisteredFamilyDoctorOfGivenPatient_UserNotFound() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                patientService.getRegisteredFamilyDoctorOfGivenPatient(username));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetRegisteredFamilyDoctorOfGivenPatient_UserIsNoPatient() {
        String username = "testuser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                patientService.getRegisteredFamilyDoctorOfGivenPatient(username));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetRegisteredFamilyDoctorOfGivenPatient_Success() throws BusinessException {
        String username = "testuser";

        User user = new User();
        Patient patient = new Patient();
        FamilyDoctor familyDoctor = new FamilyDoctor();

        patient.setFamilyDoctor(familyDoctor);
        user.setPatient(patient);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        FamilyDoctor result = patientService.getRegisteredFamilyDoctorOfGivenPatient(username);

        assertEquals(familyDoctor, result);
    }

    @Test
    void testGetAllPatientsRegisteredAtAGivenFamilyDoctor_UserNotFound() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                patientService.getAllPatientsRegisteredAtAGivenFamilyDoctor(username));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetAllPatientsRegisteredAtAGivenFamilyDoctor_UserIsNoFamilyDoctor() {
        String username = "testuser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                patientService.getAllPatientsRegisteredAtAGivenFamilyDoctor(username));

        assertEquals(BusinessExceptionCode.INVALID_USER, exception.getBusinessExceptionCode());
    }

    @Test
    void testGetAllPatientsRegisteredAtAGivenFamilyDoctor_Success() throws BusinessException {
        FamilyDoctor familyDoctor = new FamilyDoctor();

        User user = new User();
        user.setUsername("testUser");
        user.setFamilyDoctor(familyDoctor);

        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        List<Patient> patients = Arrays.asList(patient1, patient2);

        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        when(patientRepository.findAllByFamilyDoctor(familyDoctor)).thenReturn(patients);

        List<Patient> result = patientService.getAllPatientsRegisteredAtAGivenFamilyDoctor(username);

        assertEquals(patients, result);
    }

}