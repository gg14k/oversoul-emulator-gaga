package org.gaga.oversoul.data.cache;

import java.util.Map;

/**
 * Contrato base para repositories mantidos em cache.
 *
 * @param <K> tipo da chave primária
 * @param <V> tipo do valor armazenado
 */
public interface CachedRepository<K, V> {

    /**
     * Carrega os dados do banco e publica um novo snapshot.
     */
    void load();

    /**
     * Recarrega o cache.
     */
    default void reload() {
        load();
    }

    /**
     * Tenta recarregar mantendo o snapshot atual caso ocorra erro.
     */
    boolean reloadSafely();

    /**
     * Retorna um valor ou null caso não exista.
     */
    V get(K key);

    /**
     * Retorna um valor obrigatório.
     *
     * @throws IllegalStateException caso a chave não exista
     */
    V require(K key);

    /**
     * Snapshot imutável atual.
     */
    Map<K, V> snapshot();

    /**
     * Quantidade de registros.
     */
    int size();

    /**
     * Nome usado para logs e identificação.
     */
    String repoName();
}