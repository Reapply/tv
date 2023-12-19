package me.banker.tv

import org.bukkit.Color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * The DisplayCommand class is responsible for handling the display command.
 */
class DisplayCommand(private val plugin: Tv) : CommandExecutor {
    private var textDisplays: List<TextDisplay>? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("display", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("This command can only be run by a player.")
                return true
            }

            if (args.size < 2) {
                sender.sendMessage("Please specify 'on', 'gif' or 'off' and a filename.")
                return true
            }

            when (args[0].lowercase(Locale.getDefault())) {
                "on" -> handleOnCommand(sender, args[1])
                "off" -> handleOffCommand(sender)
                else -> sender.sendMessage("Invalid argument. Please specify 'on', 'gif' or 'off'.")
            }

            return true
        }

        return false
    }

    private fun handleOnCommand(sender: Player, filename: String) {
        val file = File(plugin.dataFolder, "images/$filename.png")

        if (!file.exists()) {
            sender.sendMessage("File not found: $filename.png")
            return
        }

        var image: BufferedImage = ImageIO.read(file)

        // Scale down the image to 100x100
        val scaledImage = image.getScaledInstance(150, 100, Image.SCALE_DEFAULT)
        image = BufferedImage(150, 100, BufferedImage.TYPE_INT_RGB)
        image.graphics.drawImage(scaledImage, 0, 0, null)

        val width = image.width
        val height = image.height

        // Remove the old TextDisplay
        textDisplays?.forEach { it.remove() }

        val displays = mutableListOf<TextDisplay>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = image.getRGB(x, y)
                val red = color shr 16 and 0xFF
                val green = color shr 8 and 0xFF
                val blue = color and 0xFF

                // Offset location, decrease the divisor to move closer and invert y for correct orientation
                val location = sender.location.add(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)

                val textDisplay = sender.world.spawn(location, TextDisplay::class.java).apply {
                    this.setText(" ")
                    this.lineWidth = 1
                    this.isSeeThrough = false
                    this.backgroundColor = Color.fromRGB(red, green, blue)
                }

                displays.add(textDisplay)

                // Move back player to original position for next spawn
                location.subtract(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)
            }
        }

        textDisplays = displays

        sender.sendMessage("Image displayed.")
    }

    private fun handleOffCommand(sender: CommandSender) {
        // Remove the TextDisplays
        textDisplays?.forEach { it.remove() }
        textDisplays = null
        sender.sendMessage("Display removed.")
    }
}