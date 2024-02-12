package com.example.medsynex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SignInResponseDTO {
    private String token;
    private String type;
    private Long id;
    private String username;
    private String email;
    private final List<String> roles;

    public SignInResponseDTO(String token, Long id, String username, String email, List<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}
