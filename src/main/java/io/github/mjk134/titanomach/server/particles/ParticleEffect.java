package io.github.mjk134.titanomach.server.particles;

import io.github.mjk134.titanomach.Titanomach;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;

public class ParticleEffect {
    Particles particles;
    private double ox, oy, oz;
    private float rotation = 0;
    private float angle = 0;
    private int time;
    private int counter;
    private FinishCallback callback;

    public ParticleEffect(Particles particles, double ox, double oy, double oz, int time){
        this.particles = particles;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.time = time;
    }

    public void spawnParticles() {
        for(Particle particle : particles.particles){
            particle.spawnParticleAtPosition(ox, oy, oz, angle);
        }
        angle += rotation;
        counter++;
    }


    public void setRotation(float angle) {
        rotation = angle;
    }

    public int getTime(){
        return time;
    }

    public int getCounter(){
        return counter;
    }

    public void setCallback(FinishCallback callback){
        this.callback = callback;
    }

    public FinishCallback getCallback(){
        return callback;
    }
}

