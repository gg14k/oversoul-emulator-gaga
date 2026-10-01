package org.gaga.oversoul.console.command.commands;

import org.gaga.oversoul.console.command.ConsoleCommand;
import org.gaga.oversoul.data.cache.CachedRepository;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.utils.GameLogger;

public final class ReloadCommand
        implements ConsoleCommand {

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String description() {
        return "Reloads a game cache.";
    }

    @Override
    public String usage() {
        return "reload <cache|all>";
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            GameLogger.info(
                    "Usage: {}",
                    usage()
            );

            return;
        }

        GameCaches gameCaches =
                GlobalService.gameCaches();

        if (gameCaches == null) {
            GameLogger.warning(
                    "Game caches are not initialized."
            );

            return;
        }

        String target =
                args[0];

        if ("all".equalsIgnoreCase(target)) {
            reloadAll(gameCaches);
            return;
        }

        reloadCache(
                gameCaches,
                target
        );
    }

    private void reloadAll(
            GameCaches gameCaches
    ) {
        GameLogger.info(
                "Reloading all game caches..."
        );

        boolean success =
                gameCaches.reloadAll();

        if (success) {
            GameLogger.info(
                    "All game caches reloaded successfully."
            );

            return;
        }

        GameLogger.warning(
                "One or more game caches failed to reload."
        );
    }

    private void reloadCache(
            GameCaches gameCaches,
            String target
    ) {
        CachedRepository<?, ?> repository =
                gameCaches
                        .registry()
                        .find(target);

        if (repository == null) {
            GameLogger.warning(
                    "Unknown game cache: {}",
                    target
            );

            return;
        }

        GameLogger.info(
                "Reloading game cache '{}'...",
                repository.repoName()
        );

        boolean success =
                repository.reloadSafely();

        if (success) {
            GameLogger.info(
                    "Game cache '{}' reloaded successfully.",
                    repository.repoName()
            );

            return;
        }

        GameLogger.warning(
                "Game cache '{}' failed to reload.",
                repository.repoName()
        );
    }
}