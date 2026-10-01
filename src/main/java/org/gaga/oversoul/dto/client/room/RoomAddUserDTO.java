package org.gaga.oversoul.dto.client.room;

public record RoomAddUserDTO(
        int status,
        String type,
        String cmd,
        String senderName,
        RoomInfoDTO room,
        RoomPlayerDTO player,
        String location
) {
}