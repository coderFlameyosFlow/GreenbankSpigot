package org.flameyosflow.greenbank.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import org.flameyosflow.greenbank.GreenBankMain
import com.greenbank.api.commands.managers.GreenBankCommand
import com.greenbank.api.utils.MessageUtils

import java.io.IOException
import java.util.ArrayList

/**
 * DO NOT MIX THIS UP WITH Green***B***ankCommand
 *
 * @author FlameyosFlow
 */
open class GreenbankCommand(private val greenBank: GreenBankMain) : GreenBankCommand(greenBank) {
    private val commands = arrayOf("reload", "version")

    private fun configError(sender: CommandSender) {
        sender.sendMessage(MessageUtils.colorMessage("&cUh oh! something went wrong, please check console!"))
        sender.sendMessage(" ")
        sender.sendMessage(MessageUtils.colorMessage("&cMake sure your database configuration is correct!"))
        sender.sendMessage(MessageUtils.colorMessage("&cAnd make sure you didn't put any unsupported placeholders in messages.yml's messages!"))
        sender.sendMessage(MessageUtils.colorMessage("&4If you think this is not your fault, make sure to report this to the developer."))
    }

    override fun execute(sender: CommandSender, command: Command?, label: String?, args: Array<String>?): Boolean {
        if (senderDoesNotHavePermission(sender, "greenbank.admin.gb")) {
            sender.sendMessage(MessageUtils.colorMessage(NOT_ENOUGH_PERMISSIONS))
            return false
        }
        when (args!!.size) {
            0 -> sendGreenbankHelpMessage(sender)
            1 -> {
                if (args[0].equals("reload", ignoreCase = true)) {
                    try {
                        greenBank.initConfig()
                        sender.sendMessage(MessageUtils.colorMessage("&aSuccessfully reloaded all GreenBank's configuration files"))
                    } catch (error: IOException) {
                        configError(sender)
                        return false
                    }
                } else if (args[0].equals("version", ignoreCase = true))
                    sender.sendMessage(MessageUtils.colorMessage("&7Greenbank's version is: " + "&c&l" + greenBank.version))
            }
        }
        return true
    }

    override fun tabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): MutableList<String> {
        val list: ArrayList<String> = ArrayList()
        if (args!!.size == 1) {
            for (cmd in this.commands) {
                list.add(cmd)
            }
        }
        return list
    }
}