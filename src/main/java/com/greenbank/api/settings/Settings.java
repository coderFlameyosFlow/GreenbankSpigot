package com.greenbank.api.settings;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Settings implements SettingsAPI {
    @NotNull @Getter private final Settings instance;
    @NotNull private final YamlDocument configFile = greenBank.getConfigFile();

    @Contract(pure = true)
    public Settings() {
        this.instance = this;
    }

    public double getDefaultStartingBalance() {
        return configFile.getDouble("default-starting-balance");
    }

    public boolean overrideAllOtherPlugins() {
        return configFile.getBoolean("override-all-other-plugins");
    }

    public boolean shouldCreateAccountOnJoin() {
        return configFile.getBoolean("create-account-on-join");
    }

    public String getCurrencySymbol() {
        return configFile.getString("currency-symbol");
    }

    public String getCurrencyName() {
        return configFile.getString("currency-name");
    }

    public String getCurrencyNamePlural() {
        return configFile.getString("currency-name-plural");
    }

    public double getDefaultStartingBankBalance() {
        return configFile.getDouble("default-starting-bank-balance");
    }
}
