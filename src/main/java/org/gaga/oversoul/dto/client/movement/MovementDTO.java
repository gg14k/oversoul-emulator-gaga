package org.gaga.oversoul.dto.client.movement;

public record MovementDTO(
        String type,
        String cmd,
        String senderName,
        long roomID,
        String body
) {
}