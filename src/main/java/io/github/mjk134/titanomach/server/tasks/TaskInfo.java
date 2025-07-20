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
}
