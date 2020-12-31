package com.github.pulsebeat02.command;

public enum GlobalTime {

    WAR_TO_PEACEFUL(20 * 60 * 60 * 24),
    PEACEFUL_TO_WAR(20 * 60 * 20),
    COMBAT_TIMER(20 * 60 * 30);

    private final long time;
    GlobalTime(final long time) {
        this.time = time;
    }

    public long getTime() { return time; }

}
