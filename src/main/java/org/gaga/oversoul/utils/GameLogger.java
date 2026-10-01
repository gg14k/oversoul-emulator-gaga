package org.gaga.oversoul.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public final class GameLogger {

    private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private GameLogger() {}

    private static void log(Level level, String message, Object... args) {
        Class<?> callerClass = WALKER.getCallerClass();
        var logger = LoggerFactory.getLogger(callerClass);

        switch (level) {
            case INFO -> logger.info(message, args);
            case WARN -> logger.warn(message, args);
            case ERROR -> logger.error(message, args);
            case DEBUG -> logger.debug(message, args);
            case TRACE -> logger.trace(message, args);
        }
    }

    public static void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public static void warning(String message, Object... args) {
        log(Level.WARN, message, args);
    }

    public static void error(String message, Throwable throwable) {
        Class<?> callerClass = WALKER.getCallerClass();
        LoggerFactory.getLogger(callerClass).error(message, throwable);
    }

    public static void debug(String message, Object... args) {
        log(Level.DEBUG, message, args);
    }
}