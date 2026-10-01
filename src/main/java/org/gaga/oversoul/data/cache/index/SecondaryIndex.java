package org.gaga.oversoul.data.cache.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Índice secundário de relação 1:1.
 *
 * Exemplo:
 *
 * character name -> CharacterDefinition
 */
public final class SecondaryIndex<V, IK>
        implements CacheIndexDefinition<V> {

    public enum DuplicatePolicy {
        FAIL,
        KEEP_FIRST,
        KEEP_LAST
    }

    private final String name;
    private final Function<V, IK> keyExtractor;
    private final DuplicatePolicy duplicatePolicy;
    private final Supplier<Map<?, ?>> snapshotSupplier;

    public SecondaryIndex(
            String name,
            Function<V, IK> keyExtractor,
            DuplicatePolicy duplicatePolicy,
            Supplier<Map<?, ?>> snapshotSupplier
    ) {
        this.name = Objects.requireNonNull(name);
        this.keyExtractor = Objects.requireNonNull(keyExtractor);
        this.duplicatePolicy = Objects.requireNonNull(duplicatePolicy);
        this.snapshotSupplier = Objects.requireNonNull(snapshotSupplier);
    }

    @Override
    public String name() {
        return name;
    }

    public V get(IK key) {
        return snapshot().get(key);
    }

    public V require(IK key) {
        V value = get(key);

        if (value == null) {
            throw new IllegalStateException(
                    "Missing key '" + key + "' in index " + name
            );
        }

        return value;
    }

    public int size() {
        return snapshot().size();
    }

    @SuppressWarnings("unchecked")
    public Map<IK, V> snapshot() {
        return (Map<IK, V>) snapshotSupplier.get();
    }

    @Override
    public Map<IK, V> build(Collection<V> values) {
        Map<IK, V> next =
                new HashMap<>(Math.max(16, values.size() * 2));

        for (V value : values) {
            IK key = keyExtractor.apply(value);

            if (key == null) {
                continue;
            }

            V previous = next.put(key, value);

            if (previous == null) {
                continue;
            }

            switch (duplicatePolicy) {
                case FAIL -> throw new IllegalStateException(
                        "Duplicate key in index '"
                                + name
                                + "': "
                                + key
                );

                case KEEP_FIRST ->
                        next.put(key, previous);

                case KEEP_LAST -> {
                    // valor novo já substituiu o anterior
                }
            }
        }

        return Collections.unmodifiableMap(next);
    }
}