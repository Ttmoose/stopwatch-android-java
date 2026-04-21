package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class AlarmingState implements StopwatchState {

    private final StopwatchSMStateView sm;

    public AlarmingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    @Override
    public void onAction() {
        sm.actionStopAlarm();
        sm.resetTickCount();
        sm.actionResetTime();
        sm.toStoppedState();
    }

    @Override
    public void onSetTime(final int time) {
        // The direct-entry field is disabled while the alarm is active.
    }

    @Override
    public void onTick() {
        // The alarm continues until the user acknowledges it.
    }

    @Override
    public void updateView() {
        sm.updateUITime();
    }

    @Override
    public int getId() {
        return R.string.ALARMING;
    }
}
