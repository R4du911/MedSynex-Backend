package com.example.medsynex.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BusinessExceptionCode {
    INVALID_CREDENTIALS("INVALID CREDENTIALS","Invalid username or password"),
    MISSING_COOKIE("MISSING_COOKIE", "Cookie is missing"),
    EXPIRED_REFRESH_TOKEN("EXPIRED_REFRESH_TOKEN", "Session has expired"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Session is invalid");

    private String errorId;

    private String message;
}
