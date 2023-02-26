package org.flameyosflow.greenbank.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.errors.CannotFindUserException
import com.greenbank.api.commands.managers.GreenBankCommand
import com.greenbank.api.utils.MessageUtils
import com.greenbank.api.utils.StringUtils


import java.util.ArrayList

/**
 * Administrative commands to handle some green money!
 *
 * @author FlameyosFlow
 * @since 1.0.0 BUILD 1
 */
class EcoCommand(private val greenBank: GreenBankMain) : GreenBankCommand(greenBank) {
    private val economy = greenBank.economy

    private val commands = arrayOf("give", "set", "reset", "remove")

    @Throws(CannotFindUserException::class)
    private fun givePlayer(sender: CommandSender, player: Player, amount: Double) {
        if (playerHasNotPlayedBefore(player)) throw CannotFindUserException(PLAYER_DOES_NOT_EXIST)
        val deposit = economy.depositPlayer(player, amount)
        if (deposit.transactionSuccess()) {
            val givenMoneySuccess = greenBank.messagesConfigFile.getString("money-given-success")
            StringUtils.replace(givenMoneySuccess, "%amount%", amount.toString())
            StringUtils.replace(givenMoneySuccess, "%player%", player.name)
            sender.sendMessage(MessageUtils.colorMessage(givenMoneySuccess))
            val playerGivenMoneySuccess = greenBank.messagesConfigFile.getString("given-money-success")
            StringUtils.replace(playerGivenMoneySuccess, "%amount%", amount.toString())
            StringUtils.replace(playerGivenMoneySuccess, "%total%", (economy.getBalance(player) - amount).toString())
            player.sendMessage(MessageUtils.colorMessage(playerGivenMoneySuccess))
        }
    }

    @Throws(CannotFindUserException::class)
    private fun removePlayer(sender: CommandSender, player: Player, amount: Double) {
        if (playerHasNotPlayedBefore(player)) throw CannotFindUserException(PLAYER_DOES_NOT_EXIST)
        val withdraw = economy.withdrawPlayer(player, amount)
        if (withdraw.transactionSuccess()) {
            val removedMoneySuccess = greenBank.messagesConfigFile.getString("removed-money-success")
            StringUtils.replace(removedMoneySuccess, "%amount%", amount.toString())
            StringUtils.replace(removedMoneySuccess, "%player%", player.name)
            sender.sendMessage(MessageUtils.colorMessage(removedMoneySuccess))
            val playerRemovedMoneySuccess = greenBank.messagesConfigFile.getString("removed-money-success")
            StringUtils.replace(playerRemovedMoneySuccess, "%amount%", amount.toString())
            StringUtils.replace(playerRemovedMoneySuccess, "%total%", (economy.getBalance(player) - amount).toString())
            player.sendMessage(MessageUtils.colorMessage(playerRemovedMoneySuccess))
        }
    }

    @Throws(CannotFindUserException::class)
    private fun setPlayer(sender: CommandSender, player: Player, amount: Double) {
        if (playerHasNotPlayedBefore(player)) throw CannotFindUserException(PLAYER_DOES_NOT_EXIST)
        greenBank.databaseConnect.setBalance(player.uniqueId, amount)
        val setMoneySuccess = greenBank.messagesConfigFile.getString("set-money-success")
        StringUtils.replace(setMoneySuccess, "%amount%", amount.toString())
        StringUtils.replace(setMoneySuccess, "%player%", player.name)
        sender.sendMessage(MessageUtils.colorMessage(setMoneySuccess))
        val playerSetMoneySuccess = greenBank.messagesConfigFile.getString("player-money-set-success")
        StringUtils.replace(playerSetMoneySuccess, "%amount%", amount.toString())
        player.sendMessage(MessageUtils.colorMessage(playerSetMoneySuccess))
    }

    @Throws(CannotFindUserException::class)
    private fun resetPlayer(sender: CommandSender, player: Player) {
        if (playerHasNotPlayedBefore(player)) throw CannotFindUserException(PLAYER_DOES_NOT_EXIST)
        greenBank.databaseConnect.setBalance(player.uniqueId, greenBank.configFile.getDouble("default-starting-balance"))
        sender.sendMessage(MessageUtils.colorMessage(greenBank.messagesConfigFile.getString("reset-money-success")))
        player.sendMessage(MessageUtils.colorMessage(greenBank.messagesConfigFile.getString("player-money-reset-success")))
    }

    override fun execute(sender: CommandSender, command: Command?, label: String?, args: Array<String>?): Boolean {
        // If the player doesn't have permission then forget this command ever happened.
        if (args!!.size != 3) {
            sender.sendMessage(MessageUtils.colorMessage("&cUsage: /eco <command> <player> <amount>"))
            return false
        }
        if (senderDoesNotHavePermission(sender, "greenbank.admin.eco")) {
            sender.sendMessage(MessageUtils.colorMessage(NOT_ENOUGH_PERMISSIONS))
            return false
        }
        val playerTwo = Bukkit.getPlayer(args[1]) ?: return false
        val playerTwoName = playerTwo.name
        val amount = args[2].toDouble()
        val amountToString = amount.toString()
        if (args[0].equals("give", ignoreCase = true) &&
            args[1].equals(playerTwoName, ignoreCase = true) &&
            args[2].equals(amountToString, ignoreCase = true)) {
            try { givePlayer(sender, playerTwo, amount)  } catch (error: CannotFindUserException) { sender.sendMessage(MessageUtils.colorMessage(PLAYER_DOES_NOT_EXIST)) }
        } else if (args[0].equals("remove", ignoreCase = true) &&
                   args[1].equals(playerTwoName, ignoreCase = true) &&
                   args[2].equals(amountToString, ignoreCase = true)) {
            try { removePlayer(sender, playerTwo, amount) } catch (error: CannotFindUserException) { sender.sendMessage(MessageUtils.colorMessage(PLAYER_DOES_NOT_EXIST)) }
        } else if (args[0].equals("set", ignoreCase = true) &&
                   args[1].equals(playerTwoName, ignoreCase = true) &&
                   args[2].equals(amountToString, ignoreCase = true)) {
            try { setPlayer(sender, playerTwo, amount) } catch (error: CannotFindUserException) { sender.sendMessage(MessageUtils.colorMessage(PLAYER_DOES_NOT_EXIST)) }
        } else if (args[0].equals("reset", ignoreCase = true) &&
                   args[1].equals(playerTwoName, ignoreCase = true) &&
                   args[2].equals(amountToString, ignoreCase = true)) {
            try { resetPlayer(sender, playerTwo) } catch (error: CannotFindUserException) { sender.sendMessage(MessageUtils.colorMessage(PLAYER_DOES_NOT_EXIST)) }
        } else { sendEconomyHelpMessage(sender) }
        return true
    }

    override fun tabComplete(sender: CommandSender,command: Command, alias: String, args: Array<out String>?): MutableList<String> {
        val list: ArrayList<String> = ArrayList()
        if (args?.size == 1)
            for (arg in commands) list.add(arg)
        if (args?.size == 2)
            for (player in Bukkit.getOnlinePlayers()) list.add(player.name)
        return list
    }
}
