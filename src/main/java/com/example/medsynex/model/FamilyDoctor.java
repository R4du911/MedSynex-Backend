package com.example.medsynex.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "family_doctor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyDoctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispensary_id")
    private Dispensary dispensary;

    private Integer nrPatients;
}
