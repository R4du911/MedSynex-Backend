package com.example.medsynex.model.dto;

import com.example.medsynex.model.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private ERole role;
}
