package com.example.medsynex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table (name = "consultations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_cnp")
    private Patient patient;

    private LocalDate createDate;
    private LocalDate updateDate;

    private String doctorFirstName;
    private String doctorLastName;
    private String doctorUsername;

    private String diagnosis;
    private String remarks;
    private String medications;
}
