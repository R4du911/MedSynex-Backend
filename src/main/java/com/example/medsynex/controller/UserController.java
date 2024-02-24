package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import com.example.medsynex.model.dto.LoginRequestDTO;
import com.example.medsynex.model.dto.RegisterRequestDTO;
import com.example.medsynex.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AuthController authenticator;

    @Autowired
    UserDetailsServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) throws BusinessException {
        this.userService.registerUser(registerRequest);
        return this.authenticator.authenticateUser(new LoginRequestDTO(registerRequest.getUsername(), registerRequest.getPassword()));
    }
}
