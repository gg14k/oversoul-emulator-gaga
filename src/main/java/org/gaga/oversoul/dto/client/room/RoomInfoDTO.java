package org.gaga.oversoul.dto.client.room;

public record RoomInfoDTO(
        long id,
        String name,
        String filename,
        int instance
) { }