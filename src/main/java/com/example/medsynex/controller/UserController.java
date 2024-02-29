package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.Laboratory;
import com.example.medsynex.model.dto.LoginRequestDTO;
import com.example.medsynex.model.dto.RegisterAsDoctorRequestDTO;
import com.example.medsynex.model.dto.RegisterRequestDTO;
import com.example.medsynex.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthController authenticator;

    @Autowired
    private UserDetailsServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) throws BusinessException {
        this.userService.registerUser(registerRequest);
        return this.authenticator.authenticateUser(new LoginRequestDTO(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    @PostMapping("/register/doctor/{username}")
    public ResponseEntity<String> registerUserAsDoctor(@PathVariable String username, @RequestBody RegisterAsDoctorRequestDTO registerAsDoctorRequestDTO) throws BusinessException {
        this.userService.registerUserAsDoctor(username, registerAsDoctorRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register/laboratory/{username}")
    public ResponseEntity<String> registerUserAsLaboratory(@PathVariable String username, @RequestBody Laboratory laboratory) throws BusinessException {
        this.userService.registerUserAsLaboratory(username, laboratory);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
