package com.example.medsynex.dto.consultation;

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
