package io.github.mjk134.titanomach.server.event;

public abstract class Event {
    protected int durationTicks;

    public Event(int durationTicks) {
        this.durationTicks = durationTicks;
        onStart();
    }

    public void tick() {
        durationTicks--;
    }

    public boolean isFinished() {
        return durationTicks <= 0;
    }

    /// Broadcast messages
    public abstract void onStart();

    /// Broadcast messages after ticks finish
    public abstract void onEnd();

}
