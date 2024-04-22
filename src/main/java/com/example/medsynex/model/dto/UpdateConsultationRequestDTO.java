package com.example.medsynex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateConsultationRequestDTO {
    private Long id;
    private String diagnosis;
    private String remarks;
    private String medications;
}
