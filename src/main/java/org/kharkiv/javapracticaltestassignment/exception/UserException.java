package org.kharkiv.javapracticaltestassignment.exception;

import org.springframework.http.HttpStatus;

public record UserException(String message, Throwable throwable, HttpStatus httpStatus) {}
