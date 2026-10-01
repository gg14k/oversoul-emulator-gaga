package org.gaga.oversoul.dto.client.chat;

public record ChatMessageDTO(
        String type,
        String cmd,
        String senderName,
        long roomID,
        String body
) {
}