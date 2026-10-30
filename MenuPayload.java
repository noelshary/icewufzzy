package de.icewfuzzy;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Menü -> Server: Einstellung ändern ("follow"=true) oder Aktion ("summon", "remove"). */
public record MenuPayload(String key, String value) implements CustomPayload {
    public static final CustomPayload.Id<MenuPayload> ID = new CustomPayload.Id<>(Identifier.of("icewfuzzy", "menu"));
    public static final PacketCodec<RegistryByteBuf, MenuPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING, MenuPayload::key,
        PacketCodecs.STRING, MenuPayload::value,
        MenuPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
}
