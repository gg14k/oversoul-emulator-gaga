package org.gaga.oversoul.dto.client.room;

import java.util.List;

public record RoomJoinDTO(
        int status,
        String type,
        String cmd,
        String senderName,
        RoomInfoDTO room,
        List<RoomPlayerDTO> list
) {}