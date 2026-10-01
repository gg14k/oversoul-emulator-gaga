package org.gaga.oversoul.packet.request;

/**
 * Lançada quando um packet recebido do client possui
 * dados ausentes ou em formato inválido.
 */
public final class InvalidPacketException extends RuntimeException {

    public InvalidPacketException(String message) {
        super(message);
    }
}