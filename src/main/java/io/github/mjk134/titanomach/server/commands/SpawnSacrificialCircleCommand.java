package io.github.mjk134.titanomach.server.commands;

import com.mojang.brigadier.context.CommandContext;
import io.github.mjk134.titanomach.Titanomach;
import io.github.mjk134.titanomach.server.particles.ParticleEffect;
import io.github.mjk134.titanomach.server.particles.ParticleManager;
import io.github.mjk134.titanomach.server.particles.Particles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import com.google.gson.Gson;
import net.minecraft.text.Text;

import java.io.FileReader;
import java.io.IOException;

import static io.github.mjk134.titanomach.Titanomach.*;

public class SpawnSacrificialCircleCommand {
    public static int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(SpawnSacrificialCircleCommand.class.getResource("/SacCircleParticles.json").getPath())) {

            // convert the JSON data to a Java object
            Particles particles = gson.fromJson(reader, Particles.class);
            ParticleEffect particleEffect = new ParticleEffect(particles, source.getPosition().x, source.getPosition().y, source.getPosition().z, 120);
            particleEffect.setRotation(0.03f);
            source.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 1000000));
            source.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 120, 1000000));
            source.getPlayer().sendMessage(Text.of("YOU HAVE BEEN SENTENCED TO DIE"), true);
            particleEffect.setCallback(() -> {
                for (int i = 0; i <= 100; i++) {
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, SERVER_INSTANCE.getOverworld());
                    lightning.setPosition(source.getPosition());
                    SERVER_INSTANCE.getOverworld().spawnEntity(lightning);
                }
                source.getPlayer().kill(SERVER_INSTANCE.getOverworld());
            });

            ParticleManager.addParticleEffect(particleEffect);


        } catch (IOException e) {
            MOD_LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }
}
