package org.gaga.oversoul.data.cache.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Índice secundário de relação 1:N.
 *
 * Exemplo:
 *
 * elementId -> List<CharacterDefinition>
 */
public final class GroupIndex<V, IK>
        implements CacheIndexDefinition<V> {

    private final String name;
    private final Function<V, IK> keyExtractor;
    private final Supplier<Map<?, ?>> snapshotSupplier;

    public GroupIndex(
            String name,
            Function<V, IK> keyExtractor,
            Supplier<Map<?, ?>> snapshotSupplier
    ) {
        this.name = Objects.requireNonNull(name);
        this.keyExtractor = Objects.requireNonNull(keyExtractor);
        this.snapshotSupplier = Objects.requireNonNull(snapshotSupplier);
    }

    @Override
    public String name() {
        return name;
    }

    public List<V> get(IK key) {
        return snapshot().getOrDefault(
                key,
                List.of()
        );
    }

    public int groups() {
        return snapshot().size();
    }

    @SuppressWarnings("unchecked")
    public Map<IK, List<V>> snapshot() {
        return (Map<IK, List<V>>) snapshotSupplier.get();
    }

    @Override
    public Map<IK, List<V>> build(Collection<V> values) {
        Map<IK, List<V>> next =
                new HashMap<>(Math.max(16, values.size() * 2));

        for (V value : values) {
            IK key = keyExtractor.apply(value);

            if (key == null) {
                continue;
            }

            next.computeIfAbsent(
                    key,
                    ignored -> new ArrayList<>()
            ).add(value);
        }

        next.replaceAll(
                (key, list) ->
                        Collections.unmodifiableList(list)
        );

        return Collections.unmodifiableMap(next);
    }
}