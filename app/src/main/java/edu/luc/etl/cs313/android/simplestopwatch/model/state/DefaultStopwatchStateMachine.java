package edu.luc.etl.cs313.android.simplestopwatch.model.state;

import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;

/**
 * An implementation of the state machine for the stopwatch.
 *
 * @author laufer
 */
public class DefaultStopwatchStateMachine implements StopwatchStateMachine {

    private final TimeModel timeModel;

    private final ClockModel clockModel;

    private final StopwatchState stoppedState = new StoppedState(this);

    private final StopwatchState waitingState = new WaitingState(this);

    private final StopwatchState runningState = new RunningState(this);

    private final StopwatchState alarmingState = new AlarmingState(this);

    private int tickCount = 0;

    private boolean clockRunning = false;

    private StopwatchState state;

    private StopwatchModelListener listener;

    public DefaultStopwatchStateMachine(final TimeModel timeModel, final ClockModel clockModel) {
        this.timeModel = timeModel;
        this.clockModel = clockModel;
    }

    protected void setState(final StopwatchState state) {
        this.state = state;
        if (listener != null) {
            listener.onStateUpdate(state.getId());
        }
    }

    @Override
    public void setModelListener(final StopwatchModelListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void onAction() {
        state.onAction();
    }

    @Override
    public synchronized void onSetTime(final int time) {
        state.onSetTime(time);
    }

    @Override
    public synchronized void onTick() {
        state.onTick();
    }

    @Override
    public void updateUITime() {
        listener.onTimeUpdate(timeModel.getTime());
    }

    @Override
    public void toAlarmingState() {
        setState(alarmingState);
    }

    @Override
    public void toRunningState() {
        setState(runningState);
    }

    @Override
    public void toWaitingState() {
        setState(waitingState);
    }

    @Override
    public void toStoppedState() {
        setState(stoppedState);
    }

    @Override
    public void actionInit() {
        actionStopClock();
        actionStopAlarm();
        resetTickCount();
        toStoppedState();
        actionResetTime();
    }

    @Override
    public void actionResetTime() {
        timeModel.resetTime();
        actionUpdateView();
    }

    @Override
    public void actionSetTime(final int time) {
        timeModel.setTime(time);
        actionUpdateView();
    }

    @Override
    public void actionIncrementTime() {
        timeModel.incrementTime();
        actionUpdateView();
    }

    @Override
    public void actionDecrementTime() {
        timeModel.decrementTime();
        actionUpdateView();
    }

    @Override
    public void actionStartClock() {
        if (!clockRunning) {
            clockModel.start();
            clockRunning = true;
        }
    }

    @Override
    public void actionStopClock() {
        if (clockRunning) {
            clockModel.stop();
            clockRunning = false;
        }
    }

    @Override
    public void actionUpdateView() {
        state.updateView();
    }

    @Override
    public void actionBeep() {
        listener.playBeep();
    }

    @Override
    public void actionStartAlarm() {
        listener.startAlarmSound();
    }

    @Override
    public void actionStopAlarm() {
        listener.stopAlarmSound();
    }

    @Override
    public void incTickCount() {
        tickCount++;
    }

    @Override
    public int getTickCount() {
        return tickCount;
    }

    @Override
    public void resetTickCount() {
        tickCount = 0;
    }

    @Override
    public boolean isTimeZero() {
        return timeModel.isZero();
    }

    @Override
    public boolean isTimeMax() {
        return timeModel.isMax();
    }
}
