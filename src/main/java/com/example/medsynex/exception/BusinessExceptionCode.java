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
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Session is invalid"),
    INVALID_DATA("INVALID_DATA", "Data is invalid"),
    INVALID_USER_FORMAT("INVALID_USER_FORMAT", "User has an invalid format"),
    USERNAME_ALREADY_REGISTERED("USERNAME_ALREADY_REGISTERED", "User already registered with this username"),
    EMAIL_ALREADY_REGISTERED("EMAIL_ALREADY_REGISTERED","User already registered with this email"),
    INVALID_USER("INVALID_USER", "User is not registered"),
    PATIENT_ALREADY_REGISTERED("PATIENT_ALREADY_REGISTERED", "Patient with same CNP already exists"),
    FAMILY_DOCTOR_HAS_MAX_PATIENTS("FAMILY_DOCTOR_HAS_MAX_PATIENTS", "Family doctor already has maximum number of patients"),
    FAMILY_DOCTOR_REQUEST_ALREADY_EXISTS("FAMILY_DOCTOR_REQUEST_ALREADY_EXISTS", "A family doctor request with this patient and family doctor already exists"),
    INVALID_PATIENT("INVALID_PATIENT", "Patient not found");

    private String errorId;

    private String message;
}
