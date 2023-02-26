package com.greenbank.api.errors;

public class CannotFindUserException extends Exception {
    public CannotFindUserException(String error) {
        super(error);
    }
}