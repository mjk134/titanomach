package io.github.mjk134.titanomach.server.particles;

import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Colors;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Identifier;

import java.awt.*;

public class Particle {
    double x, y, z, r, g, b, scale;
    void spawnParticleAtPosition(double tx, double ty, double tz, double rotation) {
        CommandManager commandManager = Titanomach.SERVER_INSTANCE.getCommandManager();
        double fx = tx + (x * Math.cos(rotation) -  z * Math.sin(rotation));
        double fy = ty + y;
        double fz = tz + (x * Math.sin(rotation) +  z * Math.cos(rotation));
        int color = (int)(b*255) | (int)(g*255) << 8 | (int)(r*255) << 16;

        Titanomach.SERVER_INSTANCE.getOverworld().spawnParticles(new DustParticleEffect(color, (float) scale), fx, fy, fz, 1, 0.0, 0.0, 0.0, 0.1);
        //commandManager.executeWithPrefix(Titanomach.SERVER_INSTANCE.getCommandSource(), "particle minecraft:dust{color:["+r+","+g+","+b+",],scale:"+scale+"} "+fx+" "+fy+" "+fz+" 0 0 0 0 1 normal");
    }
}
