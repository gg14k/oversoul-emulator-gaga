package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.dto.client.chat.ChatMessageDTO;
import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.world.room.GameRoom;

public final class ChatModule implements IPacketModule {

    @Override
    public void handle(PlayerEntity player, PacketData packet) {
        String command = packet.getString("cmd");

        switch (command) {
            case "list" -> handleList(player, packet);
            case "m" -> handleMessage(player, packet);
            default -> {
                // Other chat commands will be implemented later.
            }
        }
    }

    private void handleList(PlayerEntity player, PacketData packet) {
        long roomId = packet.getLong("roomID");
        GlobalService.roomService().sendRoomList(player, roomId);
    }

    private void handleMessage(PlayerEntity player, PacketData packet) {
        long roomId = packet.getLong("roomID");
        String body = packet.getString("body");

        if (body == null || body.isBlank()) return;
        if (player.getContext().getCurrentRoomId() != roomId) return;

        GameRoom room = GlobalService.roomService().getRoom(roomId);

        if (room == null) return;

        ChatMessageDTO response = new ChatMessageDTO("chat", "m", player.getProfileData().displayName(), room.id(), body);
        room.broadcast(response);
    }
}