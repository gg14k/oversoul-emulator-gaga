package org.gaga.oversoul.data.cache;

import org.gaga.oversoul.data.cache.index.CacheIndexDefinition;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.data.cache.index.SecondaryIndex;
import org.gaga.oversoul.database.DatabaseConnectionHandler;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.utils.GameLogger;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Repository jOOQ com cache imutável e índices derivados.
 *
 * O cache principal e todos os índices são construídos antes
 * de um único snapshot ser publicado.
 */
public abstract class AbstractJooqCachedRepository<K, V>
        implements CachedRepository<K, V> {

    private final DatabaseKey databaseKey;

    private final List<CacheIndexDefinition<V>> indexes =
            new ArrayList<>();

    private final AtomicReference<CacheState<K, V>> state =
            new AtomicReference<>(
                    new CacheState<>(
                            Map.of(),
                            Map.of()
                    )
            );

    protected AbstractJooqCachedRepository(
            DatabaseKey databaseKey
    ) {
        this.databaseKey =
                Objects.requireNonNull(databaseKey);
    }

    /**
     * Contexto jOOQ usado pelo repository.
     */
    protected final DSLContext dsl() {
        return DatabaseConnectionHandler.dsl(
                databaseKey
        );
    }

    /**
     * Busca os registros no banco.
     */
    protected abstract List<V> fetchAll(
            DSLContext dsl
    );

    /**
     * Chave primária utilizada pelo cache.
     */
    protected abstract K keyOf(V value);

    /**
     * Registra um índice secundário 1:1.
     */
    protected final <IK> SecondaryIndex<V, IK> secondaryIndex(
            String name,
            Function<V, IK> keyExtractor,
            SecondaryIndex.DuplicatePolicy duplicatePolicy
    ) {
        ensureUniqueIndex(name);

        SecondaryIndex<V, IK> index =
                new SecondaryIndex<>(
                        name,
                        keyExtractor,
                        duplicatePolicy,
                        () -> indexSnapshot(name)
                );

        indexes.add(index);

        return index;
    }

    /**
     * Registra um índice secundário 1:N.
     */
    protected final <IK> GroupIndex<V, IK> groupIndex(
            String name,
            Function<V, IK> keyExtractor
    ) {
        ensureUniqueIndex(name);

        GroupIndex<V, IK> index =
                new GroupIndex<>(
                        name,
                        keyExtractor,
                        () -> indexSnapshot(name)
                );

        indexes.add(index);

        return index;
    }

    @Override
    public final void load() {
        long start =
                System.currentTimeMillis();

        List<V> rows =
                List.copyOf(
                        fetchAll(dsl())
                );

        Map<K, V> primary =
                buildPrimaryCache(rows);

        Map<String, Map<?, ?>> builtIndexes =
                buildIndexes(rows);

        /*
         * Somente chegamos aqui se:
         *
         * - query funcionou
         * - mapping funcionou
         * - primary keys são válidas
         * - não existem duplicidades inválidas
         * - todos os índices foram construídos
         *
         * Agora publicamos tudo em uma única operação.
         */
        state.set(
                new CacheState<>(
                        primary,
                        builtIndexes
                )
        );

        long elapsed =
                System.currentTimeMillis() - start;

        GameLogger.info(
                "Loaded cache '{}' - {} entries ({}ms)",
                repoName(),
                primary.size(),
                elapsed
        );
    }

    @Override
    public final boolean reloadSafely() {
        try {
            load();
            return true;

        } catch (Exception e) {
            GameLogger.error(
                    "Failed to reload cache '"
                            + repoName()
                            + "'. Previous snapshot preserved.",
                    e
            );

            return false;
        }
    }

    @Override
    public final V get(K key) {
        return state
                .get()
                .primary()
                .get(key);
    }

    @Override
    public final V require(K key) {
        V value = get(key);

        if (value == null) {
            throw new IllegalStateException(
                    repoName()
                            + " - missing key: "
                            + key
            );
        }

        return value;
    }

    @Override
    public final Map<K, V> snapshot() {
        return state
                .get()
                .primary();
    }

    @Override
    public final int size() {
        return state
                .get()
                .primary()
                .size();
    }

    @Override
    public String repoName() {
        return getClass()
                .getSimpleName()
                .replace("Repository", "");
    }

    private Map<K, V> buildPrimaryCache(
            List<V> rows
    ) {
        Map<K, V> next =
                new HashMap<>(
                        Math.max(
                                16,
                                rows.size() * 2
                        )
                );

        for (V value : rows) {
            K key = keyOf(value);

            if (key == null) {
                throw new IllegalStateException(
                        repoName()
                                + " keyOf() returned null."
                );
            }

            V previous =
                    next.put(
                            key,
                            value
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        repoName()
                                + " duplicate primary key: "
                                + key
                );
            }
        }

        return Collections.unmodifiableMap(next);
    }

    private Map<String, Map<?, ?>> buildIndexes(
            List<V> rows
    ) {
        if (indexes.isEmpty()) {
            return Map.of();
        }

        Map<String, Map<?, ?>> result =
                new LinkedHashMap<>();

        for (CacheIndexDefinition<V> index : indexes) {
            result.put(
                    index.name(),
                    index.build(rows)
            );
        }

        return Collections.unmodifiableMap(result);
    }

    private Map<?, ?> indexSnapshot(
            String name
    ) {
        return state
                .get()
                .indexes()
                .getOrDefault(
                        name,
                        Map.of()
                );
    }

    private void ensureUniqueIndex(
            String name
    ) {
        boolean exists =
                indexes.stream()
                        .anyMatch(
                                index ->
                                        index.name()
                                                .equals(name)
                        );

        if (exists) {
            throw new IllegalStateException(
                    "Duplicate index name: " + name
            );
        }
    }

    /**
     * Snapshot único contendo cache principal e todos
     * os índices derivados dele.
     */
    private record CacheState<K, V>(
            Map<K, V> primary,
            Map<String, Map<?, ?>> indexes
    ) {
    }
}