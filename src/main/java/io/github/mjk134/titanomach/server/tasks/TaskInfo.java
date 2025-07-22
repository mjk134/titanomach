package io.github.mjk134.titanomach.server.tasks;

public class TaskInfo {
    public TaskType taskType;
    public String target;
    public int maxProgress;
    public int progressPointReward;

    public TaskInfo(TaskType taskType, String target, int maxProgress, int progressPointReward) {
        this.taskType = taskType;
        this.target = target;
        this.maxProgress = maxProgress;
        this.progressPointReward = progressPointReward;
    }

    public boolean equals(Task task) {
        return task.maxProgress == maxProgress && task.progressPointReward == progressPointReward && task.targetID.equals(target);
    }

    public String generateID(String playerID) {
        return playerID + " " + this.taskType.toString() + " " + this.target + " " + this.maxProgress + " " + this.progressPointReward;
    }

    public Task createTask(String playerID) {
        return switch (taskType) {
            case TaskType.COLLECTION -> new CollectionTask(
                    generateID(playerID),
                    maxProgress,
                    progressPointReward,
                    target
            );
            case TaskType.SLAYER -> new SlayerTask(
                    generateID(playerID),
                    maxProgress,
                    progressPointReward,
                    target
            );
            case TaskType.ADVANCEMENT -> new AdvancementTask(
                    generateID(playerID),
                    progressPointReward,
                    target
            );
        };
    }
}
