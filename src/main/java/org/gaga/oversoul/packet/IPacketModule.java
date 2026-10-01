package org.gaga.oversoul.packet;

import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;

/**
 * Contrato base para módulos responsáveis pelos packets do client.
 */
public interface IPacketModule {

    void handle(
            PlayerEntity player,
            PacketData packet
    );
}