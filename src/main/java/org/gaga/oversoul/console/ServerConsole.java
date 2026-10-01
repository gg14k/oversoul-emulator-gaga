package org.gaga.oversoul.console;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

import org.gaga.oversoul.console.command.CommandRegistry;
import org.gaga.oversoul.utils.GameLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ServerConsole {

    private static final int MAX_LINES = 10_000;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Queue<ConsoleEntry> PENDING = new ConcurrentLinkedQueue<>();
    private static volatile ServerConsole instance;
    private final JTextField commandInput;
    private final CommandRegistry commandRegistry;
    private final ExecutorService commandExecutor;
    private final JFrame frame;
    private final JTextPane output;
    private final StyledDocument document;

    private ServerConsole() {
        frame = new JFrame("Oversoul Emulator Console");

        output = new JTextPane();
        output.setEditable(false);
        ConsoleStyle.configureTextPane(output);

        commandRegistry = CommandRegistry.createDefault();
        commandExecutor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "server-console-command");
                thread.setDaemon(true);
                return thread;
            }
        );

        commandInput = new JTextField();
        commandInput.setBackground(new Color(28, 28, 28));
        commandInput.setForeground(ConsoleStyle.FOREGROUND);
        commandInput.setCaretColor(ConsoleStyle.FOREGROUND);
        commandInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        commandInput.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        commandInput.setBackground(new Color(28, 28, 28));
        commandInput.setForeground(ConsoleStyle.FOREGROUND);
        commandInput.setCaretColor(ConsoleStyle.FOREGROUND);
        commandInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        commandInput.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        document = output.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBorder(null);
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(1000, 650);
        frame.setMinimumSize(new Dimension(700, 400));
        frame.setLocationRelativeTo(null);

        /*
         * Closing the console window should not terminate
         * the game server unexpectedly.
         */
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        //Command line
        JButton executeButton = new JButton("Run");
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBackground(ConsoleStyle.BACKGROUND);
        commandPanel.add(commandInput, BorderLayout.CENTER);
        commandPanel.add(executeButton, BorderLayout.EAST);
        frame.add(commandPanel, BorderLayout.SOUTH);

        commandInput.addActionListener(event -> submitCommand());
        executeButton.addActionListener(event -> submitCommand());
    }

    public static void launch() {
        if (instance != null) {
            return;
        }

        Runnable task = () -> {
            if (instance != null) {
                return;
            }

            ServerConsole console =
                    new ServerConsole();

            instance = console;

            console.frame.setVisible(true);

            console.flushPending();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
            return;
        }

        try {
            SwingUtilities.invokeAndWait(task);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize server console.",
                    e
            );
        }
    }

    public static void publish(
            ILoggingEvent event
    ) {
        ConsoleEntry entry =
                ConsoleEntry.from(event);

        ServerConsole console =
                instance;

        if (console == null) {
            PENDING.offer(entry);
            return;
        }

        console.append(entry);
    }

    public static void showConsole() {
        ServerConsole console =
                instance;

        if (console == null) {
            launch();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            console.frame.setVisible(true);
            console.frame.toFront();
        });
    }

    private void flushPending() {
        ConsoleEntry entry;

        while ((entry = PENDING.poll()) != null) {
            append(entry);
        }
    }

    private void append(
            ConsoleEntry entry
    ) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(
                    () -> append(entry)
            );

            return;
        }

        try {
            String time =
                    Instant
                            .ofEpochMilli(entry.timestamp())
                            .atZone(
                                    ZoneId.systemDefault()
                            )
                            .format(TIME_FORMAT);

            appendText(
                    "[" + time + "] ",
                    ConsoleStyle.timestampStyle()
            );

            appendText(
                    "[" + entry.thread() + "] ",
                    ConsoleStyle.threadStyle()
            );

            appendText(
                    String.format(
                            "[%-5s] ",
                            entry.level()
                    ),
                    ConsoleStyle.levelStyle(
                            entry.level()
                    )
            );

            appendText(
                    entry.message(),
                    ConsoleStyle.messageStyle(
                            entry.level()
                    )
            );

            appendText(
                    System.lineSeparator(),
                    ConsoleStyle.defaultStyle()
            );

            if (entry.throwable() != null
                    && !entry.throwable().isBlank()) {

                appendText(
                        entry.throwable(),
                        ConsoleStyle.levelStyle(
                                Level.ERROR
                        )
                );

                if (!entry.throwable()
                        .endsWith(
                                System.lineSeparator()
                        )) {

                    appendText(
                            System.lineSeparator(),
                            ConsoleStyle.defaultStyle()
                    );
                }
            }

            trimOldLines();

            output.setCaretPosition(
                    document.getLength()
            );

        } catch (BadLocationException e) {
            System.err.println(
                    "Failed to append server console log: "
                            + e.getMessage()
            );
        }
    }

    private void appendText(
            String text,
            javax.swing.text.AttributeSet style
    ) throws BadLocationException {

        document.insertString(
                document.getLength(),
                text,
                style
        );
    }

    private void trimOldLines()
            throws BadLocationException {

        Element root =
                document.getDefaultRootElement();

        int lineCount =
                root.getElementCount();

        if (lineCount <= MAX_LINES) {
            return;
        }

        int linesToRemove =
                lineCount - MAX_LINES;

        Element lastLineToRemove =
                root.getElement(
                        linesToRemove - 1
                );

        if (lastLineToRemove == null) {
            return;
        }

        int removeUntil =
                lastLineToRemove.getEndOffset();

        Document doc = document;

        doc.remove(
                0,
                Math.min(
                        removeUntil,
                        doc.getLength()
                )
        );
    }

    private record ConsoleEntry(
            long timestamp,
            String thread,
            Level level,
            String message,
            String throwable
    ) {

        private static ConsoleEntry from(
                ILoggingEvent event
        ) {
            String throwable = null;

            if (event.getThrowableProxy() != null) {
                throwable =
                        ThrowableProxyUtil.asString(
                                event.getThrowableProxy()
                        );
            }

            return new ConsoleEntry(
                    event.getTimeStamp(),
                    event.getThreadName(),
                    event.getLevel(),
                    event.getFormattedMessage(),
                    throwable
            );
        }
    }

    private void submitCommand() {
        String command =
                commandInput
                        .getText()
                        .trim();

        if (command.isEmpty()) {
            return;
        }

        commandInput.setText("");

        GameLogger.info(
                "> {}",
                command
        );

        /*
         * Commands may perform database operations such as cache reloads.
         * They must not block the Swing Event Dispatch Thread.
         */
        commandExecutor.execute(
                () ->
                        commandRegistry.execute(
                                command
                        )
        );
    }

    public static void clear() {
        ServerConsole console =
                instance;

        if (console == null) {
            return;
        }

        SwingUtilities.invokeLater(
                () -> {
                    try {
                        console.document.remove(
                                0,
                                console.document.getLength()
                        );

                    } catch (BadLocationException e) {
                        System.err.println(
                                "Failed to clear server console: "
                                        + e.getMessage()
                        );
                    }
                }
        );
    }
}