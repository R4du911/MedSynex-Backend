package com.example.medsynex.dto.auth;

import lombok.Data;

@Data
public class SignInResponseDTO {
    private String accessToken;
    private String type;
    private boolean firstLogin;

    public SignInResponseDTO(String accessToken, boolean firstLogin) {
        this.accessToken = accessToken;
        this.type = "Bearer";
        this.firstLogin = firstLogin;
    }

}
