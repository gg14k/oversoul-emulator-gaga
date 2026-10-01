package org.gaga.oversoul.console.command.commands;

import org.gaga.oversoul.console.ServerConsole;
import org.gaga.oversoul.console.command.ConsoleCommand;

import java.util.List;

public final class ClearCommand
        implements ConsoleCommand {

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public List<String> aliases() {
        return List.of("cls");
    }

    @Override
    public String description() {
        return "Clears the graphical console.";
    }

    @Override
    public String usage() {
        return "clear";
    }

    @Override
    public void execute(String[] args) {
        ServerConsole.clear();
    }
}