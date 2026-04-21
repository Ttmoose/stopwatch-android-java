package edu.luc.etl.cs313.android.simplestopwatch.model.time;

/**
 * The passive data model of the timer.
 * It does not emit any events.
 *
 * @author laufer
 */
public interface TimeModel {
    void resetTime();
    void setTime(int time);
    void incrementTime();
    void decrementTime();
    int getTime();
    boolean isZero();
    boolean isMax();
}
