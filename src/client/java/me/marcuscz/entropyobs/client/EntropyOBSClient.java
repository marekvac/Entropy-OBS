package me.marcuscz.entropyobs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class EntropyOBSClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static EntropyOBSClient instance;
    public static OverlayWebSocket socket;
    public long lastSent = 0;
    public static final int SEND_DELAY = 100;

    private static final String MOD_DIR_NAME = "EntropyOBS";
    private static final String[] RESOURCE_FILES = {
            "index.html",
            "MinecraftiaCZSK.ttf"
    };


    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Entropy OBS Client Mod");
        instance = this;
        copyResources();
    }

    private void copyResources() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path modDir = gameDir.resolve("mods").resolve(MOD_DIR_NAME);

        try {
            Files.createDirectories(modDir);

            for (String fileName : RESOURCE_FILES) {
                copyResourceFromJar(fileName, modDir.resolve(fileName));
                LOGGER.info("Copied resource file: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyResourceFromJar(String filename, Path targetPath) throws IOException {
        if (Files.exists(targetPath)) {
            return;
        }

        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("entropyobs");
        if (container.isEmpty()) {
            throw new FileNotFoundException("Failed to get mod container for entropyobs");
        }

        Optional<Path> maybePath = container.get().findPath(filename);
        if (maybePath.isEmpty()) {
            throw new FileNotFoundException("Failed to get resource from mod container");
        }

        Path modFilePath = maybePath.get();

        Files.copy(modFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
