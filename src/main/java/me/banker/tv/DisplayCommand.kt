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
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import org.bukkit.Color
import org.bukkit.entity.Display

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
                "on" -> {
                    val filename = args[1]
                    val file = File(plugin.dataFolder, "images/$filename.png")

                    if (!file.exists()) {
                        sender.sendMessage("File not found: $filename.png")
                        return true
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
                                // Set the rgb color of the text display as the settext
                                this.setText(" ")
                                this.setLineWidth(1)
                                this.setSeeThrough(false)
                                this.setBackgroundColor(Color.fromRGB(red, green, blue))
                            }

                            displays.add(textDisplay)

                            // Move back player to original position for next spawn
                            location.subtract(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)
                        }
                    }

                    textDisplays = displays

                    sender.sendMessage("Image displayed.")
                }

                "gif" -> {
                    val filename = args[1]
                    val file = File(plugin.dataFolder, "images/$filename.gif")

                    if (!file.exists()) {
                        sender.sendMessage("File not found: $filename.gif")
                        return true
                    }

                    val imageReader: ImageReader = ImageIO.getImageReadersBySuffix("gif").next()
                    val imageInputStream: ImageInputStream = ImageIO.createImageInputStream(file)
                    imageReader.setInput(imageInputStream, false)

                    val numImages = imageReader.getNumImages(true)

                    // Remove the old TextDisplays
                    textDisplays?.forEach { it.remove() }

                    for (i in 0 until numImages) {
                        var image: BufferedImage = imageReader.read(i)

                        // Scale down the image to 100x100
                        val scaledImage = image.getScaledInstance(150, 100, Image.SCALE_DEFAULT)
                        image = BufferedImage(150, 100, BufferedImage.TYPE_INT_RGB)
                        image.graphics.drawImage(scaledImage, 0, 0, null)

                        val width = image.width
                        val height = image.height

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
                                    // Set the rgb color of the text display as the settext
                                    this.setText(" ")
                                    this.setLineWidth(1)
                                    this.setSeeThrough(false)
                                    this.setBackgroundColor(Color.fromRGB(red, green, blue))
                                }

                                displays.add(textDisplay)

                                // Move back player to original position for next spawn
                                location.subtract(x.toDouble()/10.0, (height - y).toDouble()/10.0, 0.0)
                            }
                        }

                        textDisplays = displays

                        // Delay between frames
                        Thread.sleep(100)
                    }

                    sender.sendMessage("GIF displayed.")
                }

                "off" -> {
                    // Remove the TextDisplays
                    textDisplays?.forEach { it.remove() }
                    textDisplays = null
                    sender.sendMessage("Display removed.")
                }

                else -> sender.sendMessage("Invalid argument. Please specify 'on', 'gif' or 'off'.")
            }

            return true
        }

        return false
    }
}