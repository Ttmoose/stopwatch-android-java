package edu.luc.etl.cs313.android.simplestopwatch.model.state;

/**
 * The restricted view states have of their surrounding state machine.
 * This is a client-specific interface in Peter Coad's terminology.
 *
 * @author laufer
 */
interface StopwatchSMStateView {

    // transitions
    void toAlarmingState();
    void toRunningState();
    void toWaitingState();
    void toStoppedState();

    // actions
    void actionInit();
    void actionResetTime();
    void actionSetTime(int time);
    void actionIncrementTime();
    void actionDecrementTime();
    void actionStartClock();
    void actionStopClock();
    void actionUpdateView();
    void actionBeep();
    void actionStartAlarm();
    void actionStopAlarm();

    // state-dependent UI updates
    void updateUITime();

    // waiting-state helpers
    void incTickCount();
    int getTickCount();
    void resetTickCount();
    boolean isTimeZero();
    boolean isTimeMax();
}
