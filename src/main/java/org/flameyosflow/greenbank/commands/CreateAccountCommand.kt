package org.flameyosflow.greenbank.commands

import com.greenbank.api.commands.managers.GreenBankCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.utils.MessageUtils

class CreateAccountCommand(private val greenBank: GreenBankMain) : GreenBankCommand(greenBank)  {
    override fun execute(sender: CommandSender, command: Command?, label: String?, args: Array<String>?): Boolean {
        if (greenBank.settings.shouldCreateAccountOnJoin() || sender !is Player)
            return true
        val economy = greenBank.economy
        if (economy.hasAccount(sender)) {
            sender.sendMessage(MessageUtils.colorMessage("&cYou already have an account to begin with!"))
            return true
        }
        economy.createPlayerAccount(sender)
        sender.sendMessage(MessageUtils.colorMessage("&2You've created an account successfully!"))
        return true
    }

    override fun tabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): MutableList<String>? {
        return null
    }
}