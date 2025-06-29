package io.github.mjk134.titanomach.server.tasks;

public class SlayerTask extends Task{
    private final String targetMob;
    public SlayerTask(String name, int maxProgress, int progressPointReward, boolean isGlobal, String targetMob) {
        super(name, maxProgress, progressPointReward, isGlobal);
        this.targetMob = targetMob;
    }

    public SlayerTask(String name, int maxProgress, int progressPointReward, String targetMob) {
        super(name, maxProgress, progressPointReward);
        this.targetMob = targetMob;
    }
}
