package com.greenbank.api.hooks.economy;

import lombok.Getter;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.greenbank.api.utils.MessageUtils;
import org.flameyosflow.greenbank.GreenBankMain;
import com.greenbank.api.database.DatabaseHandler;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VaultEconomySupport implements Economy {
    private final GreenBankMain greenBank;
    private final DatabaseHandler databaseConnect;
    @Getter private final VaultEconomySupport instance;

    public VaultEconomySupport(GreenBankMain greenBank) {
        this.greenBank = greenBank;
        this.databaseConnect = greenBank.getDatabaseConnect();
        this.instance = this;
    }

    @Override
    public boolean isEnabled() {
        return greenBank.isEnabled();
    }

    @Override
    public String getName() {
        return "GreenBank Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    @NotNull
    public String format(double amount) {
        if (amount < 1000) return Double.toString(amount);
        char[] letters = {'K', 'M', 'B', 'T', 'P', 'E'};
        double value = amount;
        int index = 0;
        do {
            value /= 1000;
            index++;
        } while (value / 1000 >= 1);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        return String.format("%s%s", decimalFormat.format(value), letters[index]);
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String playerId) {
        return databaseConnect.playerNotNull(UUID.fromString(playerId));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return databaseConnect.playerNotNull(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerId, String worldName) {
        return hasAccount(playerId);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerId) {
        return hasAccount(playerId) ? databaseConnect.getBalance(UUID.fromString(playerId)) : 0.0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return hasAccount(player) ? databaseConnect.getBalance(player.getUniqueId()) : 0.0;
    }

    @Override
    public double getBalance(String playerId, String world) {
        return getBalance(playerId);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerId, double amount) {
        return getBalance(playerId) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerId, String worldName, double amount) {
        return has(playerId, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerId, double amount) {
        @NotNull UUID uuid = UUID.fromString(playerId);
        double balance = databaseConnect.getBalance(uuid);
        if (!hasAccount(playerId)) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("player-does-not-exist")));
        } else try {
            databaseConnect.setBalance(uuid, balance - amount);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("money-paid-success")));
        } catch (Exception error) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("not-enough-money")));
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        @NotNull UUID uuid = player.getUniqueId();
        double balance = databaseConnect.getBalance(uuid);
        if (!hasAccount(player)) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("player-does-not-exist")));
        } else try {
            databaseConnect.setBalance(uuid, balance - amount);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("money-paid-success")));
        } catch (Exception error) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("not-enough-money")));
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerId, double amount) {
        @NotNull UUID uuid = UUID.fromString(playerId);
        double balance = databaseConnect.getBalance(uuid);
        if (!hasAccount(playerId)) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("player-does-not-exist")));
        } else try {
            databaseConnect.setBalance(uuid, balance + amount);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("received-money-success")));
        } catch (Exception error) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("not-enough-money")));
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        @NotNull UUID uuid = player.getUniqueId();
        double balance = databaseConnect.getBalance(uuid);
        if (!hasAccount(player)) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("player-does-not-exist")));
        } else try {
            databaseConnect.setBalance(uuid, balance + amount);
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("received-money-success")));
        } catch (Exception error) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, MessageUtils.colorMessage(greenBank.getMessagesConfigFile().getString("not-enough-money")));
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerId, String worldName, double amount) {
        return depositPlayer(playerId, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerId) {
        return databaseConnect.addNewPlayer(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(playerId))));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return databaseConnect.addNewPlayer((Player) player);
    }

    @Override
    public boolean createPlayerAccount(@NotNull String playerId, String worldName) {
        return createPlayerAccount(playerId);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
