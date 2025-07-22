package io.github.mjk134.titanomach.server.tasks;

public enum TaskType {
    COLLECTION,
    ADVANCEMENT,
    SLAYER;

    public static String presentVerb(TaskType taskType) {
        return switch (taskType) {
            case COLLECTION -> "collect";
            case SLAYER -> "slay";
            case ADVANCEMENT -> "achieve";
        };
    }

    public static String pastVerb(TaskType taskType) {
        return switch (taskType) {
            case COLLECTION -> "collected";
            case SLAYER -> "slain";
            case ADVANCEMENT -> "achieved";
        };
    }

    public static TaskType get(Task task) {
        if (task instanceof CollectionTask || task instanceof GlobalCollectionTask) return COLLECTION;
        if (task instanceof SlayerTask) return SLAYER;
        if (task instanceof AdvancementTask) return ADVANCEMENT;
        throw new IllegalArgumentException("Invalid task type");
    }
}
