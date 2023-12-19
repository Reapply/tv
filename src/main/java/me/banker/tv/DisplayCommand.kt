package me.banker.tv

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import org.bukkit.Color

/**
 * DisplayCommand is a class that represents a command executor for the "display" command.
 * It allows players to display an image using TextDisplays.
 *
 * @property plugin The instance of the Tv plugin.
 *
 * @constructor Creates a DisplayCommand with the given Tv plugin instance.
 */
class DisplayCommand(private val plugin: Tv) : CommandExecutor {
    private var textDisplay: TextDisplay? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("display", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("This command can only be run by a player.")
                return true
            }

            if (args.size < 2) {
                sender.sendMessage("Please specify 'on' or 'off' and a filename.")
                return true
            }

            when (args[0].lowercase(Locale.getDefault())) {
                "on" -> {
                    val filename = args[1]
                    val file = File(plugin.dataFolder, "$filename.png")

                    if (!file.exists()) {
                        sender.sendMessage("File not found: $filename.png")
                        return true
                    }

                    val image: BufferedImage = ImageIO.read(file)
                    val width = image.width
                    val height = image.height

                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            val color = image.getRGB(x, y)

                            // Spawn a new TextDisplay at the player's location
                            textDisplay = sender.world.spawn(sender.location.add(x.toDouble(), y.toDouble(), 0.0), TextDisplay::class.java).apply {
                                this.setText("\u2B1B") // Unicode square
                                this.setLineWidth(1)
                                this.setSeeThrough(false)
                                this.setBackgroundColor(Color.fromRGB(color))
                            }
                        }
                    }

                    sender.sendMessage("Image displayed.")
                }
                "off" -> {
                    // Remove the TextDisplay
                    textDisplay?.remove()
                    textDisplay = null
                    sender.sendMessage("Image removed.")
                }
                else -> sender.sendMessage("Invalid argument. Please specify 'on' or 'off'.")
            }

            return true
        }

        return false
    }
}