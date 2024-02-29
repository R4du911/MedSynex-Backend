package com.example.medsynex.service;

import com.example.medsynex.controller.AuthController;
import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.exception.BusinessExceptionCode;
import com.example.medsynex.model.*;
import com.example.medsynex.model.dto.RegisterAsDoctorRequestDTO;
import com.example.medsynex.model.dto.RegisterRequestDTO;
import com.example.medsynex.repository.DoctorRepository;
import com.example.medsynex.repository.RoleRepository;
import com.example.medsynex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.print.Doc;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;

    @Value("${security.decipherKey}")
    private String key;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
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

        Role role = roleRepository.findByName(registerRequest.getRole()).orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER_FORMAT));

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

    public void registerUserAsDoctor(String username, RegisterAsDoctorRequestDTO registerAsDoctorRequestDTO) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        Doctor doctorToSave = Doctor.builder()
                .hospital(registerAsDoctorRequestDTO.getHospital())
                .specialization(registerAsDoctorRequestDTO.getSpecialization())
                .build();

        doctorRepository.save(doctorToSave);

        user.setFirstLogin(false);
        user.setDoctor(doctorToSave);

        userRepository.save(user);
    }

    public void registerUserAsLaboratory(String username, Laboratory laboratory) throws BusinessException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionCode.INVALID_USER));

        user.setFirstLogin(false);
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
