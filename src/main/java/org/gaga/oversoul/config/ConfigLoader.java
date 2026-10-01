package org.gaga.oversoul.config;

import com.google.gson.JsonSyntaxException;
import org.gaga.oversoul.utils.JsonUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigLoader {

    private static final Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    private ConfigLoader() {}

    /**
     * Load config file into a DTO and cache it by type.
     *
     * @param file the config path
     * @param type the DTO class
     */
    public static <T> T load(Path file, Class<T> type) {
        try (FileReader reader = new FileReader(file.toFile())) {

            T config = JsonUtil.snakeGson().fromJson(reader, type);

            if (config == null) {
                System.out.printf("Config file parsed as null: %s (%s)%n", file.getFileName(), type.getSimpleName());
                throw new RuntimeException("Config parsed as null: " + file);
            }
            CACHE.put(type, config);
            System.out.printf("Loaded config file: %s", file.getFileName());
            return config;

        } catch (FileNotFoundException e) {
            System.out.printf("Config file not found: %s", file.toAbsolutePath());
            throw new RuntimeException("Config not found: " + file, e);
        } catch (JsonSyntaxException e) {
            System.out.printf("JSON syntax error in: %s - %s", file.getFileName(), e.getMessage());
            throw new RuntimeException("JSON error: " + file, e);
        } catch (Exception e) {
            System.out.printf("Error loading config file: %s - %s", file.getFileName(), e.getMessage());
            throw new RuntimeException("Error loading: " + file, e);
        }
    }

    /**
     * Get loaded instance of a specific config class.
     *
     * @param type the config class
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        return (T) CACHE.get(type);
    }

    /**
     * Reload a config file and replace the cached instance.
     *
     * @param file the config path
     * @param type the DTO class
     * @return the reloaded config instance
     */
    public static <T> T reload(Path file, Class<T> type) {
        CACHE.remove(type);
        return load(file, type);
    }
}