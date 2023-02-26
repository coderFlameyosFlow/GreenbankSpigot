package org.flameyosflow.greenbank.listeners;

import com.greenbank.api.hooks.economy.VaultEconomySupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.flameyosflow.greenbank.GreenBankMain;

public class Listeners implements Listener {
    private final GreenBankMain greenBank;
    private final VaultEconomySupport economy;

    public Listeners(GreenBankMain greenBank) {
        this.greenBank = greenBank;
        this.economy = greenBank.getEconomy();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (greenBank.getSettings().shouldCreateAccountOnJoin() && !economy.hasAccount(player))
            economy.createPlayerAccount(player);
    }
}
