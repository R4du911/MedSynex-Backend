package com.example.medsynex.controller;

import com.example.medsynex.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Map<String, String> handleInvalidArgument(BusinessException exception) {
        Map<String, String> map =  new HashMap<>();

        map.put("errorCode", String.valueOf(exception.getBusinessExceptionCode()));
        return map;

    }
}
