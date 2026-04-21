package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class RunningState implements StopwatchState {

    private final StopwatchSMStateView sm;

    public RunningState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    @Override
    public void onAction() {
        sm.actionStopClock();
        sm.resetTickCount();
        sm.actionResetTime();
        sm.toStoppedState();
    }

    @Override
    public void onSetTime(final int time) {
        // The direct-entry field is disabled while the timer is active.
    }

    @Override
    public void onTick() {
        sm.actionDecrementTime();
        if (sm.isTimeZero()) {
            sm.actionStopClock();
            sm.toAlarmingState();
            sm.actionStartAlarm();
        }
    }

    @Override
    public void updateView() {
        sm.updateUITime();
    }

    @Override
    public int getId() {
        return R.string.RUNNING;
    }
}
