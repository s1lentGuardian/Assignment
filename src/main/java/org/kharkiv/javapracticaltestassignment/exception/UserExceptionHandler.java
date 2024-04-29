package org.kharkiv.javapracticaltestassignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = {UserBadRequestException.class})
    public ResponseEntity<Object> handleUserBadRequestException(UserBadRequestException userBadRequestException) {
        UserException userException = new UserException(userBadRequestException.getMessage(),
                userBadRequestException.getCause(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        UserException userException = new UserException(userNotFoundException.getMessage(),
                userNotFoundException.getCause(),
                HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(userException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {UserIllegalArgumentException.class})
    public ResponseEntity<Object> handleUserIllegalArgumentException(UserIllegalArgumentException userIllegalArgumentException) {
        UserException userException = new UserException(userIllegalArgumentException.getMessage(),
                userIllegalArgumentException.getCause(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(userException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleException(Exception exception) {
        String errorMessage = "An unexpected error occurred";
        UserException userException = new UserException(errorMessage,
                exception.getCause(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(userException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
