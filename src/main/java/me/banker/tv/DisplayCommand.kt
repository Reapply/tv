package me.banker.tv

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import java.awt.image.BufferedImage
import java.awt.Image
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import org.bukkit.Color
import org.bukkit.entity.Display

/**
 * The DisplayCommand class is an implementation of the CommandExecutor interface
 * that handles the /display command.
 *
 * @param plugin The Tv plugin instance
 */
class DisplayCommand(private val plugin: Tv) : CommandExecutor {
    private var textDisplay: TextDisplay? = null

    /**
     * Handles the "display" command.
     *
     * @param sender The sender of the command.
     * @param command The command that was executed.
     * @param label The alias used for the command.
     * @param args The arguments passed with the command.
     * @return true if the command was executed successfully, false otherwise.
     */
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
                    val file = File(plugin.dataFolder, "images/$filename.png")

                    if (!file.exists()) {
                        sender.sendMessage("File not found: $filename.png")
                        return true
                    }
                    var image: BufferedImage = ImageIO.read(file)

                    // Scale down the image to 100x100
                    val scaledImage = image.getScaledInstance(100, 100, Image.SCALE_DEFAULT)
                    image = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
                    image.graphics.drawImage(scaledImage, 0, 0, null)

                    val width = image.width
                    val height = image.height

                    // Remove the old TextDisplay
                    textDisplay?.remove()

                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            val color = image.getRGB(x, y)
                            val red = color shr 16 and 0xFF
                            val green = color shr 8 and 0xFF
                            val blue = color and 0xFF

                            // Offset location, decrease the divisor to move closer and invert y for correct orientation
                            val location = sender.location.add(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)

                            textDisplay = sender.world.spawn(location, TextDisplay::class.java).apply {
                                // Set the rgb color of the text display as the settext
                                this.setText(" ")
                                this.setLineWidth(1)
                                this.setSeeThrough(false)
                                this.setBackgroundColor(Color.fromRGB(red, green, blue))
                            }

                            // Move back player to original position for next spawn
                            location.subtract(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)
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