package org.gaga.oversoul.packet.registry;

import org.gaga.oversoul.config.ConfigRegistry;
import org.gaga.oversoul.dto.config.PacketsDTO; // Certifique-se de importar o DTO correto
import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.utils.GameLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketRegistry {

    // Thread-safe map storing the packet "type" and its corresponding wrapper
    private static final Map<String, ModuleWrapper> ROUTER = new ConcurrentHashMap<>();

    // A wrapper to hold the instantiated module alongside its DTO configuration
    public record ModuleWrapper(
            IPacketModule moduleInstance,
            PacketsDTO.PacketEntry config
    ) {}

    /**
     * Initializes the registry by consuming the pre-loaded data from ConfigRegistry.
     */
    public static void initialize() {
        GameLogger.info("Initializing dynamic packet registry...");
        ROUTER.clear();

        try {
            // Consumes the list directly from your ConfigRegistry
            for (PacketsDTO.PacketEntry entry : ConfigRegistry.PACKETS.packets) {
                registerModule(entry);
            }
            GameLogger.info("Successfully registered {} packet modules.", ROUTER.size());

        } catch (Exception e) {
            GameLogger.error("Failed to bootstrap packet registry", e);
            System.exit(1);
        }
    }

    private static void registerModule(PacketsDTO.PacketEntry entry) {
        if (entry.type == null || entry.class_name == null) return;

        try {
            // Reflection: Finds the class by its String name from the DTO
            Class<?> clazz = Class.forName(entry.class_name);

            // Verifies if the class actually implements IPacketModule
            if (!IPacketModule.class.isAssignableFrom(clazz)) {
                GameLogger.warning("Class {} does not implement IPacketModule. Skipped.", entry.class_name);
                return;
            }

            // Instantiates the class and stores it in the router cache
            IPacketModule instance = (IPacketModule) clazz.getDeclaredConstructor().newInstance();
            ROUTER.put(entry.type, new ModuleWrapper(instance, entry));

            GameLogger.debug("Registered Module -> Type: '{}' | Class: {} | Cooldown: {}ms",
                    entry.type, clazz.getSimpleName(), entry.cooldown_ms);

        } catch (Exception e) {
            GameLogger.error("Failed to instantiate module: " + entry.class_name, e);
        }
    }

    public static ModuleWrapper getWrapper(String type) {
        return ROUTER.get(type);
    }
}