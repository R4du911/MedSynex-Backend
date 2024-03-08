package com.example.medsynex.model.dto;

import com.example.medsynex.model.FamilyDoctor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterAsPatientRequestDTO {
    private Long cnp;
    private FamilyDoctor familyDoctor;
}
