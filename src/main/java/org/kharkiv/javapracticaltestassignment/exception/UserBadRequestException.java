package org.kharkiv.javapracticaltestassignment.exception;

public class UserBadRequestException extends RuntimeException {

    public UserBadRequestException(String message) {
        super(message);
    }
}
