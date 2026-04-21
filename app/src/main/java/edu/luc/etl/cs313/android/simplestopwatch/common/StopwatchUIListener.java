package edu.luc.etl.cs313.android.simplestopwatch.common;

/**
 * A listener for timer events coming from the UI.
 *
 * @author laufer
 */
public interface StopwatchUIListener {
    void onAction();
    void onSetTime(int time);
}
