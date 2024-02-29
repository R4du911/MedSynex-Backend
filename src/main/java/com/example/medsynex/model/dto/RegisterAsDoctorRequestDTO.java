package com.example.medsynex.model.dto;

import com.example.medsynex.model.Hospital;
import com.example.medsynex.model.Specialization;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterAsDoctorRequestDTO {
    private Hospital hospital;
    private Specialization specialization;
}
