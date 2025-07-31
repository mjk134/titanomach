package io.github.mjk134.titanomach.server.sacrifice;

import io.github.mjk134.titanomach.server.TitanomachPlayer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;
import static io.github.mjk134.titanomach.Titanomach.CONFIG;

public class SacrificeManager {
    private static final int EFFECT_INTERVAL_TICK = 50;
    private static int effectIntervalCounter = 0;
    private static final StatusEffectInstance[] EFFECTS = {
            SacrificeManager.getEffect("minecraft:bad_luck", 1),
            SacrificeManager.getEffect("minecraft:weakness", 1),
            SacrificeManager.getEffect("minecraft:infested", 1),
    };

    public static void applyDebuffsOnTick(MinecraftServer server) {
        effectIntervalCounter++;
        if (effectIntervalCounter == EFFECT_INTERVAL_TICK) {
            server.getPlayerManager().getPlayerList().forEach(player -> {
                TitanomachPlayer titanomachPlayer = CONFIG.getPlayerConfig(player);
                //  Apply status effects
                if (titanomachPlayer.sacrificeLevel > 0) {
                    for (StatusEffectInstance effect : EFFECTS) {
                        player.addStatusEffect(effect);
                    }
                }
            });
            effectIntervalCounter = 0;
        }
    }

    public static void reduceHearts(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        TitanomachPlayer titanomachPlayer = CONFIG.getPlayerConfig(player);
        double maxHealth = 20.0 - titanomachPlayer.sacrificeLevel;
        if (maxHealthAttribute != null) {
            if (maxHealthAttribute.getBaseValue() != maxHealth) {
                maxHealthAttribute.setBaseValue(maxHealth);
                player.setHealth(player.getMaxHealth());
                MOD_LOGGER.info("Set max health for " + player.getName().getString() + " to " + maxHealth);
            }
        }
    }

    public static StatusEffectInstance getEffect(String effectID, int level) {
        return new StatusEffectInstance(
                Registries.STATUS_EFFECT.getEntry(Identifier.of(effectID)).get(),
                -1, // infinite duration
                level - 1,
                false,
                false,
                false
        );
    }
}
