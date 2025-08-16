package io.github.mjk134.titanomach.server.event;

import java.util.HashSet;
import java.util.Random;

public class EventManager {
    public static HashSet<Event> activeEvents = new HashSet<>(); // We can't have duplicate events
    private static final Random RANDOM = new Random();

    /// instead of ids use the class directly
    public static boolean isEventActive(Class<? extends Event> event) {
        for (Event e : activeEvents) {
            if (event.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    public static void startRandomEvent() {
        int eventType = RANDOM.nextInt(2);
        // TODO: add events
        switch (eventType) {
            case 0:
            case 1:
            default:
        }
    }


}
