package org.gaga.oversoul.console;

import ch.qos.logback.classic.Level;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public final class ConsoleStyle {

    public static final Color BACKGROUND =
            new Color(18, 18, 18);

    public static final Color FOREGROUND =
            new Color(220, 220, 220);

    public static final Color TIMESTAMP =
            new Color(120, 120, 120);

    public static final Color THREAD =
            new Color(150, 150, 150);

    private static final SimpleAttributeSet DEFAULT_STYLE =
            createStyle(FOREGROUND, false);

    private static final SimpleAttributeSet TIMESTAMP_STYLE =
            createStyle(TIMESTAMP, false);

    private static final SimpleAttributeSet THREAD_STYLE =
            createStyle(THREAD, false);

    private static final SimpleAttributeSet INFO_STYLE =
            createStyle(new Color(220, 220, 220), true);

    private static final SimpleAttributeSet DEBUG_STYLE =
            createStyle(new Color(80, 200, 255), true);

    private static final SimpleAttributeSet WARN_STYLE =
            createStyle(new Color(255, 190, 70), true);

    private static final SimpleAttributeSet ERROR_STYLE =
            createStyle(new Color(255, 90, 90), true);

    private static final SimpleAttributeSet TRACE_STYLE =
            createStyle(new Color(150, 150, 150), true);

    private ConsoleStyle() {
    }

    public static SimpleAttributeSet defaultStyle() {
        return DEFAULT_STYLE;
    }

    public static SimpleAttributeSet timestampStyle() {
        return TIMESTAMP_STYLE;
    }

    public static SimpleAttributeSet threadStyle() {
        return THREAD_STYLE;
    }

    public static SimpleAttributeSet levelStyle(Level level) {
        if (level == null) {
            return DEFAULT_STYLE;
        }

        if (level.isGreaterOrEqual(Level.ERROR)) {
            return ERROR_STYLE;
        }

        if (level.isGreaterOrEqual(Level.WARN)) {
            return WARN_STYLE;
        }

        if (level.isGreaterOrEqual(Level.INFO)) {
            return INFO_STYLE;
        }

        if (level.isGreaterOrEqual(Level.DEBUG)) {
            return DEBUG_STYLE;
        }

        return TRACE_STYLE;
    }

    public static SimpleAttributeSet messageStyle(Level level) {
        if (level == null) {
            return DEFAULT_STYLE;
        }

        if (level.isGreaterOrEqual(Level.ERROR)) {
            return ERROR_STYLE;
        }

        return DEFAULT_STYLE;
    }

    public static void configureTextPane(JTextPane textPane) {
        textPane.setBackground(BACKGROUND);
        textPane.setForeground(FOREGROUND);
        textPane.setCaretColor(FOREGROUND);

        textPane.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        13
                )
        );
    }

    private static SimpleAttributeSet createStyle(
            Color color,
            boolean bold
    ) {
        SimpleAttributeSet style =
                new SimpleAttributeSet();

        StyleConstants.setForeground(
                style,
                color
        );

        StyleConstants.setBold(
                style,
                bold
        );

        StyleConstants.setFontFamily(
                style,
                Font.MONOSPACED
        );

        StyleConstants.setFontSize(
                style,
                13
        );

        return style;
    }
}