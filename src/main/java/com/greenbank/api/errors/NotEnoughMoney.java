package com.greenbank.api.errors;

public class NotEnoughMoney extends Exception {
    public NotEnoughMoney(String message) {
        super(message);
    }
}
