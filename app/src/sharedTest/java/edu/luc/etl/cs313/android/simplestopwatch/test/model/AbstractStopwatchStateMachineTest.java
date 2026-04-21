package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.MAX_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.ClockModel;
import edu.luc.etl.cs313.android.simplestopwatch.model.clock.TickListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.state.StopwatchStateMachine;
import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;

/**
 * Testcase superclass for the timer state machine model.
 */
public abstract class AbstractStopwatchStateMachineTest {

    private StopwatchStateMachine model;

    private UnifiedMockDependency dependency;

    @Before
    public void setUp() {
        dependency = new UnifiedMockDependency();
    }

    @After
    public void tearDown() {
        dependency = null;
    }

    protected void setModel(final StopwatchStateMachine model) {
        this.model = model;
        if (model == null) {
            return;
        }
        this.model.setModelListener(dependency);
        this.model.actionInit();
        dependency.resetSignalCounters();
    }

    protected UnifiedMockDependency getDependency() {
        return dependency;
    }

    @Test
    public void testPreconditions() {
        assertEquals(R.string.STOPPED, dependency.getState());
        assertTimeEquals(0);
        assertFalse(dependency.isClockRunning());
    }

    @Test
    public void testScenarioAutoStartAfterThreeTicks() {
        model.onAction();
        assertEquals(R.string.WAITING, dependency.getState());
        assertTimeEquals(1);
        assertTrue(dependency.isClockRunning());
        assertEquals(0, dependency.getBeepCount());

        onTickRepeat(2);
        assertEquals(R.string.WAITING, dependency.getState());
        assertTimeEquals(1);

        model.onTick();
        assertEquals(R.string.RUNNING, dependency.getState());
        assertTimeEquals(1);
        assertEquals(1, dependency.getBeepCount());
        assertTrue(dependency.isClockRunning());

        model.onTick();
        assertEquals(R.string.ALARMING, dependency.getState());
        assertTimeEquals(0);
        assertFalse(dependency.isClockRunning());
        assertTrue(dependency.isAlarmRunning());
        assertEquals(1, dependency.getAlarmStartCount());
    }

    @Test
    public void testScenarioAdditionalPressResetsWaitingWindow() {
        model.onAction();
        onTickRepeat(2);

        model.onAction();
        assertEquals(R.string.WAITING, dependency.getState());
        assertTimeEquals(2);
        assertTrue(dependency.isClockRunning());

        onTickRepeat(2);
        assertEquals(R.string.WAITING, dependency.getState());
        assertTimeEquals(2);
        assertEquals(0, dependency.getBeepCount());

        model.onTick();
        assertEquals(R.string.RUNNING, dependency.getState());
        assertEquals(1, dependency.getBeepCount());
    }

    @Test
    public void testScenarioMaximumStartsImmediately() {
        for (var i = 0; i < MAX_TIME; i++) {
            model.onAction();
        }

        assertEquals(R.string.RUNNING, dependency.getState());
        assertTimeEquals(MAX_TIME);
        assertTrue(dependency.isClockRunning());
        assertEquals(1, dependency.getBeepCount());
    }

    @Test
    public void testScenarioDirectStartFromTypedTime() {
        model.onSetTime(7);

        assertEquals(R.string.RUNNING, dependency.getState());
        assertTimeEquals(7);
        assertTrue(dependency.isClockRunning());
        assertEquals(1, dependency.getBeepCount());

        model.onTick();
        assertTimeEquals(6);
    }

    @Test
    public void testScenarioRunningCancelResetsToStopped() {
        model.onAction();
        onTickRepeat(3);
        assertEquals(R.string.RUNNING, dependency.getState());

        model.onAction();
        assertEquals(R.string.STOPPED, dependency.getState());
        assertTimeEquals(0);
        assertFalse(dependency.isClockRunning());
    }

    @Test
    public void testScenarioAlarmAcknowledgementStopsAlarm() {
        model.onAction();
        onTickRepeat(4);
        assertEquals(R.string.ALARMING, dependency.getState());
        assertTrue(dependency.isAlarmRunning());

        model.onAction();
        assertEquals(R.string.STOPPED, dependency.getState());
        assertTimeEquals(0);
        assertFalse(dependency.isAlarmRunning());
        assertEquals(1, dependency.getAlarmStopCount());
    }

    protected void onTickRepeat(final int n) {
        for (var i = 0; i < n; i++) {
            model.onTick();
        }
    }

    protected void assertTimeEquals(final int t) {
        assertEquals(t, dependency.getTime());
    }
}

class UnifiedMockDependency implements TimeModel, ClockModel, StopwatchModelListener {

    private int timeValue = -1;

    private int stateId = -1;

    private boolean clockRunning = false;

    private boolean alarmRunning = false;

    private int beepCount = 0;

    private int alarmStartCount = 0;

    private int alarmStopCount = 0;

    public int getState() {
        return stateId;
    }

    public boolean isClockRunning() {
        return clockRunning;
    }

    public boolean isAlarmRunning() {
        return alarmRunning;
    }

    public int getBeepCount() {
        return beepCount;
    }

    public int getAlarmStartCount() {
        return alarmStartCount;
    }

    public int getAlarmStopCount() {
        return alarmStopCount;
    }

    public void resetSignalCounters() {
        beepCount = 0;
        alarmStartCount = 0;
        alarmStopCount = 0;
        alarmRunning = false;
    }

    @Override
    public void onTimeUpdate(final int timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public void onStateUpdate(final int stateId) {
        this.stateId = stateId;
    }

    @Override
    public void playBeep() {
        beepCount++;
    }

    @Override
    public void startAlarmSound() {
        alarmRunning = true;
        alarmStartCount++;
    }

    @Override
    public void stopAlarmSound() {
        alarmRunning = false;
        alarmStopCount++;
    }

    @Override
    public void setTickListener(final TickListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void start() {
        clockRunning = true;
    }

    @Override
    public void stop() {
        clockRunning = false;
    }

    @Override
    public void resetTime() {
        timeValue = 0;
    }

    @Override
    public void setTime(final int time) {
        timeValue = Math.max(0, Math.min(MAX_TIME, time));
    }

    @Override
    public void incrementTime() {
        timeValue = Math.min(MAX_TIME, timeValue + 1);
    }

    @Override
    public void decrementTime() {
        timeValue = Math.max(0, timeValue - 1);
    }

    @Override
    public int getTime() {
        return timeValue;
    }

    @Override
    public boolean isZero() {
        return timeValue == 0;
    }

    @Override
    public boolean isMax() {
        return timeValue == MAX_TIME;
    }
}
