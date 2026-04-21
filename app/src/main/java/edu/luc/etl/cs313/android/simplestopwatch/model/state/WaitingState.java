package edu.luc.etl.cs313.android.simplestopwatch.model.state;
import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.WAITING_TICKS;
import edu.luc.etl.cs313.android.simplestopwatch.R;

class WaitingState implements StopwatchState {

    private final StopwatchSMStateView sm;

    WaitingState(final StopwatchSMStateView sm) {
        this.sm = sm;
    }

    @Override
    public void onAction() {
        sm.actionIncrementTime();
        sm.resetTickCount();
        sm.actionStopClock();
        sm.actionStartClock();
        if (sm.isTimeMax()) {
            sm.actionBeep();
            sm.toRunningState();
            return;
        }
        sm.toWaitingState();
    }

    @Override
    public void onSetTime(final int time) {
        // Direct entry is only allowed while stopped.
    }

    @Override
    public void onTick() {
        sm.incTickCount();
        if (sm.getTickCount() >= WAITING_TICKS) {
            sm.resetTickCount();
            sm.actionBeep();
            sm.toRunningState();
        }
    }

    @Override
    public void updateView() {
        sm.updateUITime();
    }

    @Override
    public int getId() {
        return R.string.WAITING;
    }
}