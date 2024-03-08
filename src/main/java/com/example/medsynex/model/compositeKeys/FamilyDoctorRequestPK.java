package com.example.medsynex.model.compositeKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDoctorRequestPK implements Serializable {
    private Long patient;
    private Long familyDoctor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FamilyDoctorRequestPK that)) return false;
        return Objects.equals(patient, that.patient) &&
                Objects.equals(familyDoctor, that.familyDoctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient, familyDoctor);
    }
}
