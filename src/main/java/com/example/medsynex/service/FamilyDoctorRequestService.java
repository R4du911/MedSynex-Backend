package com.example.medsynex.service;

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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        Optional<FamilyDoctorRequest> familyDoctorRequest = familyDoctorRequestRepository.findById(
                new FamilyDoctorRequestPK(user.getPatient().getCnp(), selectedFamilyDoctor.getId()));
        if(familyDoctorRequest.isPresent())
            throw new BusinessException(BusinessExceptionCode.FAMILY_DOCTOR_REQUEST_ALREADY_EXISTS);

        FamilyDoctorRequest familyDoctorRequestToSave = FamilyDoctorRequest.builder()
                .patient(user.getPatient())
                .familyDoctor(selectedFamilyDoctor)
                .build();

        familyDoctorRequestRepository.save(familyDoctorRequestToSave);
    }

    @Transactional
    public void acceptRequest(FamilyDoctorRequest familyDoctorRequest) {
        Patient patient = familyDoctorRequest.getPatient();
        patient.setFamilyDoctor(familyDoctorRequest.getFamilyDoctor());
        patientRepository.save(patient);

        FamilyDoctor familyDoctor = familyDoctorRequest.getFamilyDoctor();
        familyDoctor.setNrPatients(familyDoctor.getNrPatients() + 1);
        familyDoctorRepository.save(familyDoctor);

        familyDoctorRequestRepository.deleteFamilyDoctorRequestByPatient(patient);

        if (familyDoctor.getNrPatients() == 20) {
            familyDoctorRequestRepository.deleteFamilyDoctorRequestByFamilyDoctor(familyDoctor);
        }
    }

    public void declineRequest(FamilyDoctorRequest familyDoctorRequest) {
        familyDoctorRequestRepository.delete(familyDoctorRequest);
    }
}
