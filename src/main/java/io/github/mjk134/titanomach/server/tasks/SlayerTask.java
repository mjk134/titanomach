package io.github.mjk134.titanomach.server.tasks;

public class SlayerTask extends Task{
    private final String targetMob;
    public SlayerTask(String name, int maxProgress, String targetMob) {
        super(name, maxProgress);
        this.targetMob = targetMob;
    }
}
