package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class StoppedState implements StopwatchState {

    public StoppedState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;

    @Override
    public void onStartStop() {
        sm.actionResetRunCount();
        sm.resetTickCount();
        sm.actionReset();
        sm.actionStart();
        sm.toRunningState();
    }

    @Override
    public void onLapReset() {
        sm.actionReset();
        sm.toStoppedState();
    }

    @Override
    public void onAction() {
        sm.actionResetRunCount();
        sm.resetTickCount();
        sm.actionReset();
        sm.actionStart();
        sm.toRunningState();
    }

    @Override
    public void onTick() {
        // Ignore any stale tick that arrives after the clock was stopped.
    }

    @Override
    public void onDecrement() {
        // Ignore decrement requests while stopped.
    }

    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.STOPPED;
    }
}
