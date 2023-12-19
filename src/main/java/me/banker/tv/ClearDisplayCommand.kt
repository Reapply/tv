package me.banker.tv

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.TextDisplay

class ClearDisplayCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("clearDisplay", ignoreCase = true)) {
            if (args.isEmpty()) {
                sender.sendMessage("Please specify a world.")
                return true
            }

            val world = if (sender is ConsoleCommandSender) {
                Bukkit.getWorld(args[0])
            } else {
                sender.server.worlds[0]
            }

            if (world == null) {
                sender.sendMessage("World not found: ${args[0]}")
                return true
            }

            // Remove all TextDisplay entities in the world
            world.entities.filterIsInstance<TextDisplay>().forEach { it.remove() }
            sender.sendMessage("All TextDisplays have been removed from ${world.name}.")

            return true
        }

        return false
    }
}