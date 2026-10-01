package org.gaga.oversoul.console;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logback appender responsible for forwarding
 * logging events to the graphical server console.
 */
public final class ConsoleAppender
        extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) { return; }
        ServerConsole.publish(event);
    }
}