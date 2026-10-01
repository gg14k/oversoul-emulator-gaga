package org.gaga.oversoul.console.command;

import java.util.List;

public interface ConsoleCommand {

    String name();

    String description();

    String usage();

    default List<String> aliases() {
        return List.of();
    }

    void execute(String[] args);
}