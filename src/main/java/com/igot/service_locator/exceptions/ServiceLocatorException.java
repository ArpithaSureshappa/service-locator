package com.igot.service_locator.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ServiceLocatorException extends RuntimeException {
    private String code;
    private String message;
    private HttpStatus httpStatusCode;

    public ServiceLocatorException() {
    }

    public ServiceLocatorException(String code, String message, HttpStatus httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }


}
