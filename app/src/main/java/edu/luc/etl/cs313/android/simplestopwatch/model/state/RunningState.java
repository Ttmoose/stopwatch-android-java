package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class RunningState implements StopwatchState {

    public RunningState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;

    @Override
    public void onStartStop() {
        sm.actionStop();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        sm.actionLap();
        sm.toLapRunningState();
    }

    @Override
    public void onTick() {
        sm.actionIncCount();
        if (sm.getRunCount() >= 1) {
            sm.actionStop();
            sm.toAlarmingState();
            sm.actionRingTheAlarm();
            return;
        }
        sm.toRunningState();
    }

    @Override
    public void onAction() {
        // Ignore repeated presses while the timer is running.
    }

    @Override
    public void onDecrement() {
        // Ignore decrement requests while acting as a stopwatch.
    }

    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.RUNNING;
    }
}
