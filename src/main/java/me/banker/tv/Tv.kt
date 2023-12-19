package me.banker.tv

import me.banker.tv.commands.ClearDisplayCommand
import me.banker.tv.commands.DisplayCommand
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Tv : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        getCommand("display")?.setExecutor(DisplayCommand(this))
        getCommand("clearDisplay")?.setExecutor(ClearDisplayCommand())

        // Create the images folder if it doesn't exist
        val imagesFolder = File(dataFolder, "images")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }
    }

    override fun onDisable() {
        // delete all TextDisplay entities
        server.worlds.forEach { world ->
            world.entities.filterIsInstance<TextDisplay>().forEach { it.remove() }
        }
    }
}