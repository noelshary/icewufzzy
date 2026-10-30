package de.icewfuzzy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class IcewfuzzyMod implements ModInitializer {
    public static final String MOD_ID = "icewfuzzy";

    public static final RegistryKey<EntityType<?>> KEY =
        RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "icewfuzzy"));

    public static final EntityType<IcewfuzzyEntity> ICEWFUZZY = Registry.register(
        Registries.ENTITY_TYPE, KEY.getValue(),
        EntityType.Builder.create(IcewfuzzyEntity::new, SpawnGroup.CREATURE)
            .dimensions(0.6f, 1.8f)
            .maxTrackingRange(10)
            .build(KEY));

    @Override
    public void onInitialize() {
        IcewfuzzyConfig.load();
        FabricDefaultAttributeRegistry.register(ICEWFUZZY, IcewfuzzyEntity.createAttributes());

        PayloadTypeRegistry.playC2S().register(MenuPayload.ID, MenuPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(MenuPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = (ServerWorld) player.getEntityWorld();
            switch (payload.key()) {
                case "summon" -> {
                    IcewfuzzyEntity e = ICEWFUZZY.create(world, SpawnReason.COMMAND);
                    if (e != null) {
                        e.refreshPositionAndAngles(player.getX() + 1, player.getY(), player.getZ() + 1, 0f, 0f);
                        world.spawnEntity(e);
                    }
                }
                case "remove" -> {
                    for (var e : world.getEntitiesByType(ICEWFUZZY, x -> true)) e.discard();
                }
                default -> IcewfuzzyConfig.apply(payload.key(), payload.value());
            }
        });
    }
}
