package com.example.medsynex.service;

import com.example.medsynex.controller.AuthController;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.*;
import com.example.medsynex.dto.register.RegisterRequestDTO;
import com.example.medsynex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final FamilyDoctorRequestService familyDoctorRequestService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final FamilyDoctorRepository familyDoctorRepository;
    private final PatientRepository patientRepository;

    @Value("${security.decipherKey}")
    private String key;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                                  DoctorRepository doctorRepository, FamilyDoctorRepository familyDoctorRepository,
                                  PatientRepository patientRepository, FamilyDoctorRequestService familyDoctorRequestService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
        this.familyDoctorRepository = familyDoctorRepository;
        this.patientRepository = patientRepository;
        this.familyDoctorRequestService = familyDoctorRequestService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public User getCurrentUserDetails(String username) throws BusinessException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));
    }

    public List<User> getAllUsersWhichAreRegisteredAsFamilyDoctors() {
        return this.userRepository.findAllByFamilyDoctorIsNotNull();
    }

    public List<User> getAllUsersWhichAreRegisteredAsPatients() {
        return this.userRepository.findAllByPatientIsNotNull();
    }

    public void registerUser(RegisterRequestDTO registerRequest) throws BusinessException {
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new BusinessException(BusinessExceptionCode.USERNAME_ALREADY_REGISTERED);
        }

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new BusinessException(BusinessExceptionCode.EMAIL_ALREADY_REGISTERED);
        }

        if(!validateNames(registerRequest.getFirstName()) || !validateNames(registerRequest.getLastName())) {
            throw new BusinessException(BusinessExceptionCode.INVALID_USER_FORMAT);
        }

        if(!validateEmail(registerRequest.getEmail())) {
            throw new BusinessException(BusinessExceptionCode.INVALID_USER_FORMAT);
        }

        String decryptedPassword = AuthController.decrypt(registerRequest.getPassword(), key);
        if(!validatePassword(decryptedPassword)) {
            throw new BusinessException(BusinessExceptionCode.INVALID_USER_FORMAT);
        }

        Role role = roleRepository.findByName(registerRequest.getRole())
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER_FORMAT));

        User userToSave = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(decryptedPassword))
                .roles(Set.of(role))
                .firstLogin(true)
                .build();

        userRepository.save(userToSave);
    }

    public void registerUserAsPatient(String username, Long cnp, FamilyDoctor selectedFamilyDoctor) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        if (patientRepository.findById(cnp).isPresent()) {
            throw new BusinessException(BusinessExceptionCode.PATIENT_ALREADY_REGISTERED);
        }

        if (selectedFamilyDoctor.getNrPatients() >= 20) {
            throw new BusinessException(BusinessExceptionCode.FAMILY_DOCTOR_HAS_MAX_PATIENTS);
        }

        Patient patientToSave = Patient.builder()
                .cnp(cnp)
                .familyDoctor(null)
                .build();

        Patient patientFromDb = patientRepository.save(patientToSave);

        user.setFirstLogin(false);
        user.setPatient(patientFromDb);
        user.setFamilyDoctor(null);
        user.setDoctor(null);
        user.setLaboratory(null);

        userRepository.save(user);

        familyDoctorRequestService.makeAFamilyDoctorRequest(username, selectedFamilyDoctor);
    }


    public void registerUserAsFamilyDoctor(String username, Dispensary dispensary) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        FamilyDoctor familyDoctorToSave = FamilyDoctor.builder()
                .dispensary(dispensary)
                .nrPatients(0)
                .build();

        FamilyDoctor familyDoctorFromDb = familyDoctorRepository.save(familyDoctorToSave);

        user.setFirstLogin(false);
        user.setPatient(null);
        user.setFamilyDoctor(familyDoctorFromDb);
        user.setDoctor(null);
        user.setLaboratory(null);

        userRepository.save(user);
    }

    public void registerUserAsDoctor(String username, Hospital hospital, Specialization specialization) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        Doctor doctorToSave = Doctor.builder()
                .hospital(hospital)
                .specialization(specialization)
                .build();

        Doctor doctorFromDB = doctorRepository.save(doctorToSave);

        user.setFirstLogin(false);
        user.setPatient(null);
        user.setFamilyDoctor(null);
        user.setDoctor(doctorFromDB);
        user.setLaboratory(null);

        userRepository.save(user);
    }

    public void registerUserAsLaboratory(String username, Laboratory laboratory) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        user.setFirstLogin(false);
        user.setPatient(null);
        user.setFamilyDoctor(null);
        user.setDoctor(null);
        user.setLaboratory(laboratory);

        userRepository.save(user);
    }

    public boolean validatePassword(String password) {
        if (password == null)
            return false;
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-_+.]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    public boolean validateEmail(String mail) {
        if (mail == null)
            return false;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*[-_]?[a-zA-Z0-9]+(?:[-_][a-zA-Z0-9]+)*@([a-zA-Z0-9]+(?:[-][a-zA-Z0-9]+)*\\.)+[a-zA-Z]+$");
        Matcher matcher = pattern.matcher(mail);
        return matcher.find();
    }

    public boolean validateNames(String name) {
        if (name == null)
            return false;
        Pattern pattern = Pattern.compile("^[A-Z][a-z]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

}
