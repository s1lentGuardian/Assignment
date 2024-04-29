package org.kharkiv.javapracticaltestassignment.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserExceptionHandlerTest {

    private final UserExceptionHandler userExceptionHandler = new UserExceptionHandler();

    @Test
    void handleUserBadRequestException() {
        UserBadRequestException userBadRequestException = new UserBadRequestException("Bad request");

        ResponseEntity<Object> responseEntity = userExceptionHandler.handleUserBadRequestException(userBadRequestException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Bad request", ((UserException) responseEntity.getBody()).message());
    }

    @Test
    void handleUserNotFoundException() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("Not found");

        ResponseEntity<Object> responseEntity = userExceptionHandler.handleUserNotFoundException(userNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Not found", ((UserException) responseEntity.getBody()).message());
    }

    @Test
    void handleUserIllegalArgumentException() {
        UserIllegalArgumentException userIllegalArgumentException = new UserIllegalArgumentException("Invalid argument");

        ResponseEntity<Object> responseEntity = userExceptionHandler.handleUserIllegalArgumentException(userIllegalArgumentException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid argument", ((UserException) responseEntity.getBody()).message());
    }

    @Test
    void handleException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Object> responseEntity = userExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("An unexpected error occurred", ((UserException) responseEntity.getBody()).message());
    }


}