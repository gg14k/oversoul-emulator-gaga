package org.gaga.oversoul.dto.client.room;

import java.util.List;

public record RoomPacketDTO(
        int status,
        String type,
        String cmd,
        String senderName,
        RoomInfoDTO room,
        List<RoomPlayerDTO> list
) {
}