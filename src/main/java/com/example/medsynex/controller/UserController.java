package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.Dispensary;
import com.example.medsynex.model.Laboratory;
import com.example.medsynex.model.User;
import com.example.medsynex.model.dto.LoginRequestDTO;
import com.example.medsynex.model.dto.RegisterAsDoctorRequestDTO;
import com.example.medsynex.model.dto.RegisterAsPatientRequestDTO;
import com.example.medsynex.model.dto.RegisterRequestDTO;
import com.example.medsynex.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthController authenticator;

    @Autowired
    private UserDetailsServiceImpl userService;

    @GetMapping("/current-user/{username}")
    public ResponseEntity<User> getCurrentUserDetails(@PathVariable String username) throws BusinessException {
        return new ResponseEntity<>(this.userService.getCurrentUserDetails(username), HttpStatus.OK);
    }

    @GetMapping("/retrieve-family-doctors")
    public ResponseEntity<List<User>> getAllUsersWhichAreRegisteredAsFamilyDoctors() {
        return new ResponseEntity<>(this.userService.getAllUsersWhichAreRegisteredAsFamilyDoctors(), HttpStatus.OK);
    }

    @GetMapping("/retrieve-patients")
    public ResponseEntity<List<User>> getAllUsersWhichAreRegisteredAsPatients() {
        return new ResponseEntity<>(this.userService.getAllUsersWhichAreRegisteredAsPatients(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) throws BusinessException {
        this.userService.registerUser(registerRequest);
        return this.authenticator.authenticateUser(new LoginRequestDTO(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    @PostMapping("register/patient/{username}")
    public ResponseEntity<Void> registerUserAsPatient(@PathVariable String username, @RequestBody RegisterAsPatientRequestDTO registerAsPatientRequestDTO) throws BusinessException {
        this.userService.registerUserAsPatient(username, registerAsPatientRequestDTO.getCnp(), registerAsPatientRequestDTO.getFamilyDoctor());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register/family-doctor/{username}")
    public ResponseEntity<Void> registerUserAsFamilyDoctor(@PathVariable String username, @RequestBody Dispensary dispensary) throws BusinessException {
        this.userService.registerUserAsFamilyDoctor(username, dispensary);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register/doctor/{username}")
    public ResponseEntity<Void> registerUserAsDoctor(@PathVariable String username, @RequestBody RegisterAsDoctorRequestDTO registerAsDoctorRequestDTO) throws BusinessException {
        this.userService.registerUserAsDoctor(username, registerAsDoctorRequestDTO.getHospital(), registerAsDoctorRequestDTO.getSpecialization());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register/laboratory/{username}")
    public ResponseEntity<Void> registerUserAsLaboratory(@PathVariable String username, @RequestBody Laboratory laboratory) throws BusinessException {
        this.userService.registerUserAsLaboratory(username, laboratory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
