package com.example.medsynex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "patient")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {

    @Id
    private Long cnp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "family_doctor_id")
    private FamilyDoctor familyDoctor;
}
