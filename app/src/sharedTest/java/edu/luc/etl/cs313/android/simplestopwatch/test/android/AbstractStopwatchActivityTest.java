package edu.luc.etl.cs313.android.simplestopwatch.test.android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.android.StopwatchAdapter;

/**
 * Abstract GUI-level test superclass of essential timer scenarios.
 */
public abstract class AbstractStopwatchActivityTest {

    @Test
    public void testActivityCheckTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully", getActivity());
    }

    @Test
    public void testActivityScenarioInit() {
        getActivity().runOnUiThread(() -> assertEquals(0, getDisplayedValue()));
    }

    @Test
    public void testActivityScenarioIncrement() {
        getActivity().runOnUiThread(() -> {
            assertEquals(0, getDisplayedValue());
            assertTrue(getPrimaryButton().performClick());
            assertEquals(1, getDisplayedValue());
        });
    }

    @Test
    public void testActivityScenarioWaitingResetsAfterSecondPress() throws Throwable {
        getActivity().runOnUiThread(() -> assertTrue(getPrimaryButton().performClick()));
        runUiThreadTasks();
        Thread.sleep(2100);
        runUiThreadTasks();

        getActivity().runOnUiThread(() -> {
            assertEquals(1, getDisplayedValue());
            assertTrue(getPrimaryButton().performClick());
            assertEquals(2, getDisplayedValue());
        });
        runUiThreadTasks();
        Thread.sleep(2200);
        runUiThreadTasks();

        getActivity().runOnUiThread(() -> assertEquals(2, getDisplayedValue()));
    }

    @Test
    public void testActivityScenarioTypedStart() {
        getActivity().runOnUiThread(() -> {
            getManualTimeInput().setText("07");
            assertTrue(getPrimaryButton().performClick());
            assertEquals(7, getDisplayedValue());
        });
    }

    protected abstract StopwatchAdapter getActivity();

    protected int tvToInt(final TextView t) {
        return Integer.parseInt(t.getText().toString().trim());
    }

    protected int getDisplayedValue() {
        final TextView ts = getActivity().findViewById(R.id.seconds);
        return tvToInt(ts);
    }

    protected Button getPrimaryButton() {
        return getActivity().findViewById(R.id.onAction);
    }

    protected EditText getManualTimeInput() {
        return getActivity().findViewById(R.id.timerLabel);
    }

    protected void runUiThreadTasks() { }
}
