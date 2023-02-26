package org.flameyosflow.greenbank;

import com.greenbank.api.GreenBank;
import com.greenbank.api.commands.managers.CommandSupport;
import com.greenbank.api.commands.managers.GreenBankCommand;
import com.greenbank.api.settings.Settings;
import org.flameyosflow.greenbank.commands.*;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import org.flameyosflow.greenbank.listeners.Listeners;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GreenBankSupport extends JavaPlugin {
    Settings settings = new Settings();
    public @NotNull String getVersion() {
        return "1.0.0 build 22";
    }

    private void registerCommand(@NotNull String name, @NotNull GreenBankCommand command) {
        @NotNull PluginCommand command2 = Objects.requireNonNull(Bukkit.getPluginCommand(name));
        command2.setExecutor(command);
        command2.setTabCompleter(command);
        if (settings.overrideAllOtherPlugins()) CommandSupport.add(name, command2);
    }

    private void registerListeners(@NotNull Listener listener, @NotNull JavaPlugin plugin) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void registerAll(@NotNull GreenBankMain greenBank) {
        registerCommand("balance", new BalanceCommand(greenBank));
        registerCommand("pay", new PayCommand(greenBank));
        registerCommand("eco", new EcoCommand(greenBank));
        registerCommand("greenbank", new GreenbankCommand(greenBank));
        if (!settings.shouldCreateAccountOnJoin()) {
            getLogger().info("\"create-account-on-join\" was found false, we have registered /createaccount so players can register accounts. \n");
            getLogger().warning("YOU HAVE TO KNOW IF YOU SET THIS TO \"true\" THEN YOU HAVE TO RESTART YOUR SERVER OR IT WON'T WORK PROPERLY.");
            registerCommand("createaccount", new CreateAccountCommand(greenBank));
        }
        registerListeners(new Listeners(greenBank), greenBank);
    }

    public boolean isPaperLib() {
        try {
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
