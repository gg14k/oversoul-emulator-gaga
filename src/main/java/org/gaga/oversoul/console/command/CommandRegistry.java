package org.gaga.oversoul.console.command;

import org.gaga.oversoul.console.command.commands.CachesCommand;
import org.gaga.oversoul.console.command.commands.ClearCommand;
import org.gaga.oversoul.console.command.commands.HelpCommand;
import org.gaga.oversoul.console.command.commands.ReloadCommand;
import org.gaga.oversoul.utils.GameLogger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CommandRegistry {

    private final Map<String, ConsoleCommand> lookup =
            new LinkedHashMap<>();

    private final List<ConsoleCommand> commands =
            new ArrayList<>();

    public void register(ConsoleCommand command) {
        String name = normalize(command.name());

        if (lookup.containsKey(name)) {
            throw new IllegalStateException(
                    "Command already registered: " + name
            );
        }

        commands.add(command);
        lookup.put(name, command);

        for (String alias : command.aliases()) {
            String normalizedAlias =
                    normalize(alias);

            if (lookup.containsKey(normalizedAlias)) {
                throw new IllegalStateException(
                        "Command alias already registered: "
                                + normalizedAlias
                );
            }

            lookup.put(
                    normalizedAlias,
                    command
            );
        }
    }

    public void execute(String input) {
        if (input == null || input.isBlank()) {
            return;
        }

        String[] parts =
                input.trim().split("\\s+");

        String commandName =
                normalize(parts[0]);

        ConsoleCommand command =
                lookup.get(commandName);

        if (command == null) {
            GameLogger.warning(
                    "Unknown command: {}",
                    commandName
            );

            return;
        }

        String[] args =
                new String[
                        Math.max(
                                0,
                                parts.length - 1
                        )
                        ];

        if (parts.length > 1) {
            System.arraycopy(
                    parts,
                    1,
                    args,
                    0,
                    parts.length - 1
            );
        }

        try {
            command.execute(args);

        } catch (Exception e) {
            GameLogger.error(
                    "Failed to execute command: "
                            + command.name(),
                    e
            );
        }
    }

    public List<ConsoleCommand> commands() { return List.copyOf(commands); }

    public static CommandRegistry createDefault() {
        CommandRegistry registry = new CommandRegistry();
        registry.register(new HelpCommand(registry));
        registry.register(new ClearCommand());
        registry.register(new CachesCommand());
        registry.register(new ReloadCommand());
        return registry;
    }

    private static String normalize(String value) { return value.trim().toLowerCase(Locale.ROOT); }
}