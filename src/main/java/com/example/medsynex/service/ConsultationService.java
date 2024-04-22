package com.example.medsynex.service;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.Consultation;
import com.example.medsynex.model.Patient;
import com.example.medsynex.model.User;
import com.example.medsynex.model.dto.CreateConsultationRequestDTO;
import com.example.medsynex.model.dto.UpdateConsultationRequestDTO;
import com.example.medsynex.repository.ConsultationRepository;
import com.example.medsynex.repository.PatientRepository;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Consultation> getAllConsultationsByPatient(Long cnp) {
        return consultationRepository.findAllByPatient(cnp);
    }

    public void createConsultation(Long cnp, CreateConsultationRequestDTO createConsultationRequestDTO) throws BusinessException {
        Patient patient = patientRepository.findById(cnp)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_PATIENT));

        User user = userRepository.findByUsername(createConsultationRequestDTO.getDoctorUsername())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        Consultation consultationToSave = Consultation.builder()
                .patient(patient)
                .createDate(LocalDate.now())
                .updateDate(LocalDate.now())
                .doctorFirstName(user.getFirstName())
                .doctorLastName(user.getLastName())
                .doctorUsername(user.getUsername())
                .diagnosis(createConsultationRequestDTO.getDiagnosis())
                .remarks(createConsultationRequestDTO.getRemarks())
                .medications(createConsultationRequestDTO.getMedications())
                .build();

        consultationRepository.save(consultationToSave);
    }

    public void updateConsultation(Long cnp, UpdateConsultationRequestDTO updateConsultationRequestDTO) throws BusinessException {
        patientRepository.findById(cnp).orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_PATIENT));

        Consultation consultationFromDB = consultationRepository.findById(updateConsultationRequestDTO.getId())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_DATA));

        consultationFromDB.setUpdateDate(LocalDate.now());
        consultationFromDB.setDiagnosis(updateConsultationRequestDTO.getDiagnosis());
        consultationFromDB.setRemarks(updateConsultationRequestDTO.getRemarks());
        consultationFromDB.setMedications(updateConsultationRequestDTO.getMedications());

        consultationRepository.save(consultationFromDB);
    }
}
