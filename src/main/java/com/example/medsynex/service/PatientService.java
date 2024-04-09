package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.model.User;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    public FamilyDoctor getRegisteredFamilyDoctorOfGivenPatient(String username) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        if(user.getPatient() == null)
            throw new BusinessException(BusinessExceptionCode.INVALID_USER);

        return user.getPatient().getFamilyDoctor();
    }
}
