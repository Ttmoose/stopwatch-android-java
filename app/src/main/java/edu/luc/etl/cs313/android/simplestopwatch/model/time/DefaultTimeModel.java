package edu.luc.etl.cs313.android.simplestopwatch.model.time;

import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.MAX_TIME;
import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.SEC_PER_TICK;

/**
 * An implementation of the timer data model.
 */
public class DefaultTimeModel implements TimeModel {

    private int time = 0;

    @Override
    public void resetTime() {
        time = 0;
    }

    @Override
    public void setTime(final int time) {
        this.time = Math.max(0, Math.min(MAX_TIME, time));
    }

    @Override
    public void incrementTime() {
        time = Math.min(MAX_TIME, time + SEC_PER_TICK);
    }

    @Override
    public void decrementTime() {
        time = Math.max(0, time - SEC_PER_TICK);
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public boolean isZero() {
        return time == 0;
    }

    @Override
    public boolean isMax() {
        return time == MAX_TIME;
    }
}
