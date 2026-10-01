package org.gaga.oversoul.data.cache.index;

import java.util.Collection;
import java.util.Map;

/**
 * Contrato interno utilizado pelos cached repositories
 * para construir índices derivados do mesmo snapshot.
 */
public interface CacheIndexDefinition<V> {

    String name();

    Map<?, ?> build(Collection<V> values);
}