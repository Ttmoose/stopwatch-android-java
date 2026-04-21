package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class AlarmingState implements StopwatchState {

    public AlarmingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    private final StopwatchSMStateView sm;



    @Override
    public void onStartStop() {
        sm.actionStop();
        sm.actionResetRunCount();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        sm.actionStop();
        sm.actionResetRunCount();
        sm.toStoppedState();
    }

    @Override
    public void onTick() {
        // The alarm is triggered on entry; no periodic ticking should remain.
    }

    @Override
    public void onDecrement() {
        // Ignore decrement requests while alarm is active.
    }


    @Override
    public void onAction() {
        sm.actionStop();
        sm.actionResetRunCount();
        sm.toStoppedState();
    }


    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.ALARMING;
    }
}
