package com.example.medsynex.service;

import com.example.medsynex.model.Dispensary;
import com.example.medsynex.model.FamilyDoctor;
import com.example.medsynex.repository.FamilyDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyDoctorService {

    @Autowired
    private FamilyDoctorRepository familyDoctorRepository;

    public List<FamilyDoctor> getAllFamilyDoctorsFromAGivenDispensary(Dispensary dispensary) {
        return familyDoctorRepository.findFamilyDoctorByDispensary_Id(dispensary.getId());
    }
}
