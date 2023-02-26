package com.greenbank.api.errors;

public class CannotPerformTransactionOnSelf extends Exception {
    public CannotPerformTransactionOnSelf(String message) {
        super(message);
    }
}
