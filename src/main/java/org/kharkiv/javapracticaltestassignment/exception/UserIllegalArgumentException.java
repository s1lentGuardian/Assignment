package org.kharkiv.javapracticaltestassignment.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class UserIllegalArgumentException extends RuntimeException {

    public UserIllegalArgumentException(String message) {
        super(message);
    }

    public UserIllegalArgumentException(Errors errors) {
        super(buildErrorMessage(errors));
    }

    private static String buildErrorMessage(Errors errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : errors.getAllErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }
        return errorMessage.toString();
    }

}

