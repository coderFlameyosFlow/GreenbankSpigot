package com.greenbank.api.settings;

import dev.dejvokep.boostedyaml.YamlDocument;

import lombok.Getter;

public class DatabaseSettings implements SettingsAPI {
    @Getter private final DatabaseSettings instance;
    private final YamlDocument databaseConfig = greenBank.getDatabaseConfigFile();

    public DatabaseSettings() {
        instance = this;
    }

    public String getDatabaseType() {
        return databaseConfig.getString("database-type");
    }

    public int getDatabasePort() {
        return databaseConfig.getInt("database-port");
    }

    public int getMaximumPoolSize() {
        return databaseConfig.getInt("maximum-pool-size");
    }

    public String getMongoConnectionUri() {
        return databaseConfig.getString("mongodb-connection-uri");
    }

    public String getMongoDatabase() {
        return databaseConfig.getString("mongodb-database-name");
    }

    public String getMongoCollection() {
        return databaseConfig.getString("mongodb-database-collection");
    }
}
