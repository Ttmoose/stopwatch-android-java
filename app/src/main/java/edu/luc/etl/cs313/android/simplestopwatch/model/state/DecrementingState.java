package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.R;

class DecrementingState implements StopwatchState {

    private final DefaultStopwatchStateMachine sm;

    public DecrementingState(final DefaultStopwatchStateMachine sm) {
        this.sm = sm;
    }

    @Override
    public void onStartStop() {
        sm.actionStop();
        sm.toStoppedState();
    }

    @Override
    public void onLapReset() {
        sm.actionResetRunCount();
        sm.actionStop();
        sm.toStoppedState();
    }

    @Override
    public void onDecrement() {
        sm.actionResetRunCount();
        sm.actionStop();
        sm.toStoppedState();
    }

    @Override
    public void onAction() {
        sm.actionResetRunCount();
        sm.actionStop();
        sm.toStoppedState();
    }

    @Override
    public void onTick() {
        // Count down from the armed value and fire the alarm once it expires.
        sm.actionDecCount();
        if (sm.getRunCount() == 0) {
            sm.actionStop();
            sm.toAlarmingState();
            sm.actionRingTheAlarm();
        }
    }


    @Override
    public void updateView() {
        sm.updateUIRuntime();
    }

    @Override
    public int getId() {
        return R.string.DECREMENTING;
    }
}
