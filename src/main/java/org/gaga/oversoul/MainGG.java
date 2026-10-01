package org.gaga.oversoul;

import org.gaga.oversoul.config.ConfigRegistry;
import org.gaga.oversoul.console.ServerConsole;
import org.gaga.oversoul.network.GameServer;
import org.gaga.oversoul.utils.GameLogger;
import org.gaga.oversoul.service.GlobalService;

public final class MainGG {

    public static void main(String[] args) {
        ServerConsole.launch();
        GameLogger.info("Starting Oversoul Server emulator...");
        GlobalService global = new GlobalService();
        start();
    }

    /*
        Init game server
     */
    private static void start() {
        GameServer server = new GameServer(ConfigRegistry.NETWORK.port);
        try {
            server.start();
        } catch (Exception e) {
            GameLogger.error("Failed to start the game server", e);
        }
        GameLogger.info("Oversoul GaGa Emulator 1.0.0");
    }
}