package org.gaga.oversoul.console.command.commands;

import org.gaga.oversoul.console.command.ConsoleCommand;
import org.gaga.oversoul.data.cache.CachedRepository;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.utils.GameLogger;

public final class CachesCommand
        implements ConsoleCommand {

    @Override
    public String name() {
        return "caches";
    }

    @Override
    public String description() {
        return "Displays loaded game caches.";
    }

    @Override
    public String usage() {
        return "caches";
    }

    @Override
    public void execute(String[] args) {
        GameCaches gameCaches =
                GlobalService.gameCaches();

        if (gameCaches == null) {
            GameLogger.warning(
                    "Game caches are not initialized."
            );

            return;
        }

        GameLogger.info(
                "Loaded game caches:"
        );

        for (CachedRepository<?, ?> repository
                : gameCaches
                .registry()
                .repositories()) {

            GameLogger.info(
                    "  {} - {} entries",
                    repository.repoName(),
                    repository.size()
            );
        }
    }
}