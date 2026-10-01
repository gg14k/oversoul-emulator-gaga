package org.gaga.oversoul.packet.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Locale;
import java.util.Objects;

/**
 * Wrapper para leitura e conversão dos dados recebidos do client.
 *
 * Centraliza conversões comuns do protocolo e evita que os módulos
 * manipulem JsonObject diretamente.
 */
public final class PacketData {

    private final JsonObject data;

    private PacketData(JsonObject data) {
        this.data = Objects.requireNonNull(data);
    }

    public static PacketData of(JsonObject data) {
        return new PacketData(data);
    }

    /**
     * Verifica se o campo existe e não é null.
     */
    public boolean has(String key) {
        return data.has(key)
                && !data.get(key).isJsonNull();
    }

    /**
     * Retorna String obrigatória.
     */
    public String getString(String key) {
        JsonPrimitive value = requirePrimitive(key);

        return value.getAsString();
    }

    /**
     * Retorna String ou valor padrão caso o campo não exista.
     */
    public String getString(String key, String defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }

        return getString(key);
    }

    /**
     * Retorna int obrigatório.
     *
     * Aceita tanto:
     *
     * "123"
     *
     * quanto:
     *
     * 123
     */
    public int getInt(String key) {
        JsonPrimitive value = requirePrimitive(key);

        try {
            if (value.isNumber()) {
                return value.getAsInt();
            }

            return Integer.parseInt(
                    value.getAsString().trim()
            );

        } catch (NumberFormatException e) {
            throw invalidValue(key, value);
        }
    }

    /**
     * Retorna int ou default caso o campo não exista.
     */
    public int getInt(String key, int defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }

        return getInt(key);
    }

    /**
     * Retorna long obrigatório.
     */
    public long getLong(String key) {
        JsonPrimitive value = requirePrimitive(key);

        try {
            if (value.isNumber()) {
                return value.getAsLong();
            }

            return Long.parseLong(
                    value.getAsString().trim()
            );

        } catch (NumberFormatException e) {
            throw invalidValue(key, value);
        }
    }

    /**
     * Retorna long ou default caso o campo não exista.
     */
    public long getLong(String key, long defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }

        return getLong(key);
    }

    /**
     * Retorna boolean obrigatório.
     *
     * Aceita:
     *
     * true
     * false
     * "true"
     * "false"
     * 1
     * 0
     * "1"
     * "0"
     */
    public boolean getBoolean(String key) {
        JsonPrimitive value = requirePrimitive(key);

        if (value.isBoolean()) {
            return value.getAsBoolean();
        }

        String raw = value
                .getAsString()
                .trim()
                .toLowerCase(Locale.ROOT);

        return switch (raw) {
            case "true", "1" -> true;
            case "false", "0" -> false;

            default -> throw invalidValue(
                    key,
                    value
            );
        };
    }

    /**
     * Retorna boolean ou default caso o campo não exista.
     */
    public boolean getBoolean(
            String key,
            boolean defaultValue
    ) {
        if (!has(key)) {
            return defaultValue;
        }

        return getBoolean(key);
    }

    /**
     * Retorna um objeto JSON obrigatório.
     */
    public JsonObject getObject(String key) {
        JsonElement value = require(key);

        if (!value.isJsonObject()) {
            throw invalidType(key, "object");
        }

        return value.getAsJsonObject();
    }

    /**
     * Retorna um array JSON obrigatório.
     */
    public JsonArray getArray(String key) {
        JsonElement value = require(key);

        if (!value.isJsonArray()) {
            throw invalidType(key, "array");
        }

        return value.getAsJsonArray();
    }

    private JsonPrimitive requirePrimitive(String key) {
        JsonElement value = require(key);

        if (!value.isJsonPrimitive()) {
            throw invalidType(key, "primitive");
        }

        return value.getAsJsonPrimitive();
    }

    private JsonElement require(String key) {
        if (!has(key)) {
            throw new InvalidPacketException(
                    "Missing required packet field: " + key
            );
        }

        return data.get(key);
    }

    private InvalidPacketException invalidType(
            String key,
            String expected
    ) {
        return new InvalidPacketException(
                "Invalid type for packet field '"
                        + key
                        + "'. Expected "
                        + expected
        );
    }

    private InvalidPacketException invalidValue(
            String key,
            JsonPrimitive value
    ) {
        return new InvalidPacketException(
                "Invalid value for packet field '"
                        + key
                        + "': "
                        + value
        );
    }

    @Override
    public String toString() {
        return data.toString();
    }
}