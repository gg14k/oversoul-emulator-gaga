package org.gaga.oversoul.dto.client.room;

public record RoomDropUserDTO(
        int status,
        String type,
        String cmd,
        String senderName,
        RoomInfoDTO room,
        String strName
) {
}