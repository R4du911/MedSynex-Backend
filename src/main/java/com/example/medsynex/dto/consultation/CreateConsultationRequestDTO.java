package com.example.medsynex.dto.consultation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateConsultationRequestDTO {
    private String doctorUsername;
    private String diagnosis;
    private String remarks;
    private String medications;
}
