package org.flameyosflow.greenbank;

import dev.dejvokep.boostedyaml.YamlDocument;

import com.greenbank.api.settings.*;
import com.greenbank.api.config.*;
import com.greenbank.api.database.DatabaseHandler;
import com.greenbank.api.hooks.economy.VaultEconomySupport;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.ServicePriority;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("ConstantValue")
public class GreenBankMain extends GreenBankSupport {
    @Getter private GreenBankMain instance;
    @Getter private VaultEconomySupport economy;
    @Getter private DatabaseHandler databaseConnect;

    @Getter private YamlDocument configFile;
    @Getter private YamlDocument databaseConfigFile;
    @Getter private YamlDocument messagesConfigFile;
    @Getter private DatabaseSettings databaseSettings;
    @Getter private Settings settings;

    @Override
    public void onEnable() {
        getLogger().info("Setting instance...");
        instance = this;
        getLogger().info("Setting configuration files, in " + getVersion() + " there is: \nconfig.yml \ndb.yml \nmessages.yml");
        try {
            configFile = new Config(this).configFile();
            databaseConfigFile = new DatabaseConfig(this).databaseConfig();
            messagesConfigFile = new MessagesConfig(this).messagesConfig();
        } catch (IOException error) {
            error.printStackTrace();
            fireCriticalError("Cannot start Greenbank without the configuration files.", "Message explains it");
        }
        getLogger().info("Setting the API needs...");
        settings = new Settings().getInstance();
        databaseSettings = new DatabaseSettings().getInstance();
        economy = new VaultEconomySupport(this).getInstance();
        getLogger().info("Registering database variables...");
        databaseConnect = new DatabaseHandler(this).getInstance();
        getLogger().info("Checking for vault...");
        if (!setupEconomy()) fireCriticalError("Cannot find vault on your server, please download vault for functionality", "Please download vault:");
        getLogger().info("Checking database credentials...");
        if (databaseConnect.getConnection() != null) databaseConnect.createConnection();
        else fireCriticalError("Incorrect MongoDB Credentials", "Make sure your MongoDB URI, Database and Collection are correct in db.yml");
        getLogger().info("Registering commands and listeners...");
        registerAll(this);
        getLogger().info("Greenbank has successfully enabled!");
    }

    public boolean fireCriticalError(@NotNull String message, @NotNull String error) {
        getLogger().severe("---CRITICAL ERROR!---");
        getLogger().severe(message);
        getLogger().severe(" ");
        getLogger().severe(error + ": Disabling Greenbank...");
        getLogger().severe("If you think this is a bug or glitch please contact the owner or staff of this plugin.");
        getLogger().severe("---------------------");
        getServer().getPluginManager().disablePlugin(this);
        return true;
    }

    @Override
    public void onDisable() {
        try {
            initConfig();
            databaseConnect.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getLogger().info("[GreenBank] GreenBank has been disabled!");
        }
    }

    public void initConfig() throws IOException {
        databaseConfigFile.save();
        messagesConfigFile.save();
        configFile.save();
        databaseConfigFile.reload();
        messagesConfigFile.reload();
        configFile.reload();
    }

    public InputStream getConfigYaml(@NotNull String fileName) {
        InputStream resource;
        try { resource = getResource(fileName); } catch (Exception error) {
            throw new NullPointerException("Filename of getConfigYaml() cannot be null");
        }
        return resource;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);
        getLogger().info("Economy initialized and registered successfully.");
        return true;
    }
}
