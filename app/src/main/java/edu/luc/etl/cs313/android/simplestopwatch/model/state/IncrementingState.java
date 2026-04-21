package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class IncrementingState implements StopwatchState {

    private final StopwatchSMStateView sm;

    public IncrementingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    @Override
    public void onStartStop() {
        sm.actionStop();
        sm.resetTickCount();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        sm.actionStop();
        sm.resetTickCount();
        sm.actionResetRunCount();
        sm.toStoppedState();
    }

    @Override
    public void onAction() {
        // Each tap extends the setup window and increments the countdown target.
        sm.actionIncCount();
        sm.resetTickCount();
        if (sm.getRunCount() >= 99) {
            sm.actionBeep();
            sm.toDecrementingState();
        }
    }

    @Override
    public void onTick() {
        // Transition to countdown mode after three seconds of inactivity.
        sm.incTickCount();
        if (sm.getTickCount() >= 3) {
            sm.resetTickCount();
            sm.actionBeep();
            sm.toDecrementingState();
        }
    }

    @Override
    public void onDecrement() {
        sm.resetTickCount();
        sm.actionBeep();
        sm.toDecrementingState();
    }

    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.INCREMENTING;
    }
}
