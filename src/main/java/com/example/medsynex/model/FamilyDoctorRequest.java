package com.example.medsynex.model;

import com.example.medsynex.model.compositeKeys.FamilyDoctorRequestPK;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "family_doctor_request")
@IdClass(FamilyDoctorRequestPK.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FamilyDoctorRequest {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_cnp")
    private Patient patient;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_doctor_id")
    private FamilyDoctor familyDoctor;
}
