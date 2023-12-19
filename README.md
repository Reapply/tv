# Tv Plugin

This is a Minecraft plugin developed for Bukkit servers. It allows players to display images in the game using the `/display` command.

## Features

- Display an image in the game world using TextDisplays.
- The image is read from a PNG file in the plugin's data folder.
- The `/display on <filename>` command displays the image.
- The `/display off` command removes the displayed image.

## Installation

1. Build the plugin using Maven.
2. Copy the generated JAR file to your server's `plugins` folder.
3. Restart the server or load the plugin using a plugin manager.

## Usage

1. Put your PNG images in the `plugins/Tv/images` folder.
2. Use the `/display on <filename>` command to display an image. Replace `<filename>` with the name of your image file (without the `.png` extension).
3. Use the `/display off` command to remove the displayed image.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
