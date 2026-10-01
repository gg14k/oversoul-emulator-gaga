package org.gaga.oversoul.data.cache;

import org.gaga.oversoul.utils.GameLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry responsável pelo lifecycle dos caches globais.
 */
public final class CacheRegistry {

    private final Map<String, CachedRepository<?, ?>> repositories =
            new LinkedHashMap<>();

    /**
     * Registra um repository e retorna a própria instância.
     *
     * Isso permite:
     *
     * characters = registry.register(new CharactersRepository());
     */
    public <R extends CachedRepository<?, ?>> R register(
            R repository
    ) {
        String name =
                repository.repoName();

        CachedRepository<?, ?> previous =
                repositories.putIfAbsent(
                        name,
                        repository
                );

        if (previous != null) {
            throw new IllegalStateException(
                    "Cache already registered: " + name
            );
        }

        return repository;
    }

    /**
     * Carrega todos os caches registrados.
     *
     * No startup, qualquer erro deve interromper a inicialização.
     */
    public void loadAll() {
        long start =
                System.currentTimeMillis();

        for (CachedRepository<?, ?> repository
                : repositories.values()) {

            repository.load();
        }

        long elapsed =
                System.currentTimeMillis() - start;

        GameLogger.info(
                "All game caches loaded successfully - {} repositories ({}ms)",
                repositories.size(),
                elapsed
        );
    }

    /**
     * Recarrega um cache específico mantendo o snapshot anterior
     * caso ocorra erro.
     */
    public boolean reload(String name) {
        CachedRepository<?, ?> repository =
                find(name);

        if (repository == null) {
            GameLogger.warning(
                    "Cache not found: {}",
                    name
            );

            return false;
        }

        return repository.reloadSafely();
    }

    /**
     * Recarrega todos individualmente.
     *
     * Uma falha não impede o reload dos demais.
     */
    public boolean reloadAllSafely() {
        boolean success = true;

        for (CachedRepository<?, ?> repository
                : repositories.values()) {

            if (!repository.reloadSafely()) {
                success = false;
            }
        }

        return success;
    }

    public CachedRepository<?, ?> get(
            String name
    ) {
        return repositories.get(name);
    }

    public int size() {
        return repositories.size();
    }

    public Collection<CachedRepository<?, ?>> repositories() {
        return Collections.unmodifiableCollection(
                repositories.values()
        );
    }

    public CachedRepository<?, ?> find(
            String name
    ) {
        if (name == null) {
            return null;
        }

        return repositories
                .values()
                .stream()
                .filter(repository ->
                        repository
                                .repoName()
                                .equalsIgnoreCase(name)
                )
                .findFirst()
                .orElse(null);
    }
}