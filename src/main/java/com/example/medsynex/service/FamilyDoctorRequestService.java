package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.FamilyDoctorRequest;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.FamilyDoctorRequestRepository;
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

    public List<FamilyDoctorRequest> getAllFamilyDoctorRequest() {
        return this.familyDoctorRequestRepository.findAll();
    }

    public void makeAFamilyDoctorRequest(String username, FamilyDoctor selectedFamilyDoctor) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        FamilyDoctorRequest familyDoctorRequestToSave = FamilyDoctorRequest.builder()
                .patient(user.getPatient())
                .familyDoctor(selectedFamilyDoctor)
                .build();

        familyDoctorRequestRepository.save(familyDoctorRequestToSave);
    }
}
