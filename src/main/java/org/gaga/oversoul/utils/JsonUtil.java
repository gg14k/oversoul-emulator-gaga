package org.gaga.oversoul.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON utilities.
 *
 * Keep separate Gson instances:
 * - CONFIG_GSON: snake_case JSON <-> camelCase Java (for emulator config files).
 * - CLIENT_GSON: camelCase JSON <-> camelCase Java (for client protocol packets).
 */
public final class JsonUtil {

    private static final Gson CONFIG_GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private static final Gson CLIENT_GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    private static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    private JsonUtil () {}

    /** Gson for emulator configuration files (snake_case). */
    public static Gson snakeGson() {
        return CONFIG_GSON;
    }

    /** Gson for client protocol packets (camelCase). */
    public static Gson camelGson() {
        return CLIENT_GSON;
    }

    public static Gson gson() { return GSON; }

    public static String toJson(Object object) { return GSON.toJson(object); }
}