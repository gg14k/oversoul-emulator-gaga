package org.gaga.oversoul.packet.registry;

/* Maps each packet definition inside the JSON array */
public record PacketSpec(
        String type,
        String className,
        int cooldownMs,
        boolean debug
) {}