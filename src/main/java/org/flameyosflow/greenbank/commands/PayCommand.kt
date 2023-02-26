package org.flameyosflow.greenbank.commands

import net.milkbowl.vault.economy.EconomyResponse

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.errors.CannotFindUserException
import com.greenbank.api.errors.CannotPerformTransactionOnSelf
import com.greenbank.api.errors.NotEnoughMoney
import com.greenbank.api.commands.managers.GreenBankCommand
import com.greenbank.api.utils.MessageUtils
import com.greenbank.api.utils.StringUtils

import org.junit.Assert
import java.util.ArrayList

/**
 * Pay someone some green money!
 *
 * @author FlameyosFlow
 * @since 1.0.0 BUILD 1
 */
class PayCommand(private val greenBank: GreenBankMain) : GreenBankCommand(greenBank) {
    private val economy = greenBank.economy

    @Throws(NotEnoughMoney::class, CannotFindUserException::class, CannotPerformTransactionOnSelf::class)
    private fun payPlayer(player: Player, playerTwo: Player, amount: Double) {
        if (playerHasNotPlayedBefore(playerTwo)) throw CannotFindUserException(PLAYER_DOES_NOT_EXIST)
        if (!economy.has(player, amount)) throw NotEnoughMoney(NOT_ENOUGH_MONEY)
        if (player.uniqueId === playerTwo.uniqueId) throw CannotPerformTransactionOnSelf("Player disallowed to pay themself!")

        val economyResponse: EconomyResponse = economy.withdrawPlayer(player, amount)
        if (economyResponse.transactionSuccess()) {
            val paidMessage = greenBank.messagesConfigFile.getString("money-paid-success")
            StringUtils.replace(paidMessage, "%amount%", economy.format(amount))
            StringUtils.replace(paidMessage, "%player%", playerTwo.name)
            StringUtils.replace(paidMessage, "%total%", economy.format(economy.getBalance(player) - amount))
            player.sendMessage(MessageUtils.colorMessage(paidMessage))
        }
        val anotherEconomyResponse: EconomyResponse = economy.depositPlayer(playerTwo, amount)
        if (anotherEconomyResponse.transactionSuccess()) {
            val paidMessage = greenBank.messagesConfigFile.getString("received-money-success")
            StringUtils.replace(paidMessage, "%amount%", economy.format(amount))
            StringUtils.replace(paidMessage, "%player%", playerTwo.name)
            StringUtils.replace(paidMessage, "%total%", economy.format(economy.getBalance(player) + amount))
            playerTwo.sendMessage(MessageUtils.colorMessage(paidMessage))
        }
    }

    override fun execute(sender: CommandSender, command: Command?, label: String?, args: Array<String>?): Boolean {
        val player = sender as Player
        if (args?.size != 2) {
            player.sendMessage(MessageUtils.colorMessage("&cUsage: /pay <player> <amount>"))
            return false
        }
        if (notPlayer(sender)) {
            sender.sendMessage(ONLY_PLAYER)
            return false
        }
        if (playerDoesNotHavePermission(player, "greenbank.user.pay")) {
            sender.sendMessage(NOT_ENOUGH_PERMISSIONS)
            return false
        }
        val amount: Double?
        try { amount = args[0].toDouble() } catch (e: Exception) {
            player.sendMessage(MessageUtils.colorMessage("&cExpected number for <amount> (usage: /pay <player> <amount>), but found \"${args[1]}\"."))
            return false
        }
        val playerTwo = Bukkit.getPlayer(args[1]); Assert.assertNotNull(PLAYER_DOES_NOT_EXIST, playerTwo)
        try {
            payPlayer(player, playerTwo!!, amount)
        } catch (error: CannotFindUserException) {
            player.sendMessage(MessageUtils.colorMessage(PLAYER_DOES_NOT_EXIST))
            return false
        } catch (error: NotEnoughMoney) {
            player.sendMessage(MessageUtils.colorMessage(NOT_ENOUGH_MONEY))
            return false
        } catch (error: CannotPerformTransactionOnSelf) {
            player.sendMessage(MessageUtils.colorMessage("&cCannot perform transaction on yourself, please choose someone."))
            return false
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): MutableList<String> {
        val list: ArrayList<String> = ArrayList()
        if (args!!.size == 2) {
            for (player in Bukkit.getOnlinePlayers()) {
                list.add(player.name)
            }
        }
        return list
    }
}
