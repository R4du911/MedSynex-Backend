package com.example.medsynex.exception;

import lombok.Getter;

@Getter
public class BusinessException extends Exception {
    private final BusinessExceptionCode businessExceptionCode;

    public BusinessException(BusinessExceptionCode businessExceptionCode) {
        super(businessExceptionCode.name());
        this.businessExceptionCode = businessExceptionCode;
    }
}
