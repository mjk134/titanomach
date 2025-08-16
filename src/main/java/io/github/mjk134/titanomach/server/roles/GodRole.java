package io.github.mjk134.titanomach.server.roles;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class GodRole extends Role {
    public GodRole() {
        super("God", "filler", "minecraft:command_block", "§c", 100000);
        addRankUpReward("minecraft:netherite_ingot", 64);
        addEffect("minecraft:speed", 2);
        addEffect("minecraft:regeneration", 2);
        addEffect("minecraft:fire_resistance", 2);
        addEffect("minecraft:saturation", 1);
        addEffect("minecraft:water_breathing", 1);
        addEffect("minecraft:health_boost", 5);
        addEffect("minecraft:resistance", 4);
    }

    @Override
    public void onEffectTick(PlayerEntity player) {
        // do normal effect ticks
        super.onEffectTick(player);
        // enable creative style flight
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
        PlayerAbilities abilities = serverPlayer.getAbilities();
        if (!abilities.allowFlying) {
            abilities.allowFlying = true;
            // send packet to client to sync
            serverPlayer.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(abilities));
        }
    }
}
