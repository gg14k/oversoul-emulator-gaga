package org.gaga.oversoul.console.command.commands;

import org.gaga.oversoul.console.command.CommandRegistry;
import org.gaga.oversoul.console.command.ConsoleCommand;
import org.gaga.oversoul.utils.GameLogger;

public final class HelpCommand
        implements ConsoleCommand {

    private final CommandRegistry registry;

    public HelpCommand(
            CommandRegistry registry
    ) {
        this.registry = registry;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Displays available server commands.";
    }

    @Override
    public String usage() {
        return "help";
    }

    @Override
    public void execute(String[] args) {
        GameLogger.info(
                "Available server commands:"
        );

        for (ConsoleCommand command
                : registry.commands()) {

            GameLogger.info(
                    "  {} - {}",
                    command.usage(),
                    command.description()
            );
        }
    }
}