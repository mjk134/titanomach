package io.github.mjk134.titanomach.server.tasks;

public enum TaskType {
    COLLECTION,
    SLAYER;

    public static String presentVerb(TaskType taskType) {
        return switch (taskType) {
            case COLLECTION -> "collect";
            case SLAYER -> "slay";
        };
    }

    public static String pastVerb(TaskType taskType) {
        return switch (taskType) {
            case COLLECTION -> "collected";
            case SLAYER -> "slain";
        };
    }

    public static TaskType get(Task task) {
        if (task instanceof CollectionTask) return COLLECTION;
        if (task instanceof SlayerTask) return SLAYER;
        throw new IllegalArgumentException("Invalid task type");
    }
}
