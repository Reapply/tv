package me.banker.tv

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Tv : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        getCommand("display")?.setExecutor(DisplayCommand(this))

        // Create the images folder if it doesn't exist
        val imagesFolder = File(dataFolder, "images")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}