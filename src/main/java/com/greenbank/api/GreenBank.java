package com.greenbank.api;

import lombok.Getter;

import org.flameyosflow.greenbank.GreenBankMain;

public class GreenBank {
    @Getter private final GreenBankMain greenBank = new GreenBankMain().getInstance();
}
