package io.github.mjk134.titanomach.server.tasks;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;

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

    public static String icon(TaskType taskType) {
        return switch (taskType) {
            case COLLECTION -> "⛏";
            case SLAYER -> "\uD83D\uDDE1";
            case ADVANCEMENT -> "☆";
        };
    }

    public static TaskType get(Task task) {
        if (task instanceof CollectionTask || task instanceof GlobalCollectionTask) return COLLECTION;
        if (task instanceof SlayerTask || task instanceof GlobalSlayerTask) return SLAYER;
        if (task instanceof AdvancementTask) return ADVANCEMENT;
        MOD_LOGGER.error("Invalid task type");
        throw new IllegalArgumentException("Invalid task type");
    }
}
