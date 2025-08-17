package io.github.mjk134.titanomach.server.particles;

import java.util.ArrayList;

public class ParticleManager {
    static ArrayList<ParticleEffect> particleEffects = new ArrayList<>();
    static int counter = 0;
    public static void addParticleEffect(ParticleEffect particleEffect){
        particleEffects.add(particleEffect);
    }

    public static void onTick(){
        for(int i = 0; i < particleEffects.size(); i++){
            if (counter == 1) {
                particleEffects.get(i).spawnParticles();
                if (particleEffects.get(i).getCounter() >= particleEffects.get(i).getTime()) {
                     particleEffects.get(i).getCallback().onFinish();
                    particleEffects.remove(i);
                    i -= 1;
                };
            }
        }
        if (counter == 1) {
            counter = 0;
        }
        counter++;
    }
}
