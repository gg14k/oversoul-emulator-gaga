package org.gaga.oversoul.game.map;

/**
 * Definição global de um mapa.
 */
public record MapDefinition(
        long id,
        int instanceType,
        String filename,
        String name
) {
}