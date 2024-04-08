package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.FamilyDoctorRepository;
import com.example.medsynex.repository.FamilyDoctorRequestRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyDoctorRequestService {

    @Autowired
    private FamilyDoctorRequestRepository familyDoctorRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private FamilyDoctorRepository familyDoctorRepository;

    public List<FamilyDoctorRequest> getAllFamilyDoctorRequestForAGivenFamilyDoctor(String username) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        if(user.getFamilyDoctor() == null)
            throw new BusinessException(BusinessExceptionCode.INVALID_USER);

        return this.familyDoctorRequestRepository.findAllByFamilyDoctor(user.getFamilyDoctor());
    }

    public List<FamilyDoctorRequest> getAllFamilyDoctorRequestForAGivenPatient(String username) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        if(user.getPatient() == null)
            throw new BusinessException(BusinessExceptionCode.INVALID_USER);


        return this.familyDoctorRequestRepository.findAllByPatient(user.getPatient());
    }

    public void makeAFamilyDoctorRequest(String username, FamilyDoctor selectedFamilyDoctor) throws BusinessException {
        if (selectedFamilyDoctor.getNrPatients() >= 20) {
            throw new BusinessException(BusinessExceptionCode.FAMILY_DOCTOR_HAS_MAX_PATIENTS);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        if(user.getPatient() == null)
            throw new BusinessException(BusinessExceptionCode.INVALID_USER);

        FamilyDoctorRequest familyDoctorRequestToSave = FamilyDoctorRequest.builder()
                .patient(user.getPatient())
                .familyDoctor(selectedFamilyDoctor)
                .build();

        familyDoctorRequestRepository.save(familyDoctorRequestToSave);
    }

    public void acceptRequest(FamilyDoctorRequest familyDoctorRequest) {
        Patient patient = familyDoctorRequest.getPatient();
        patient.setFamilyDoctor(familyDoctorRequest.getFamilyDoctor());
        patientRepository.save(patient);

        FamilyDoctor familyDoctor = familyDoctorRequest.getFamilyDoctor();
        familyDoctor.setNrPatients(familyDoctor.getNrPatients() + 1);
        familyDoctorRepository.save(familyDoctor);

        familyDoctorRequestRepository.delete(familyDoctorRequest);
        familyDoctorRequestRepository.deleteFamilyDoctorRequestByPatient(patient);

        if (familyDoctor.getNrPatients() == 20) {
            familyDoctorRequestRepository.deleteFamilyDoctorRequestByFamilyDoctor(familyDoctor);
        }
    }

    public void declineRequest(FamilyDoctorRequest familyDoctorRequest) {
        familyDoctorRequestRepository.delete(familyDoctorRequest);
    }
}
