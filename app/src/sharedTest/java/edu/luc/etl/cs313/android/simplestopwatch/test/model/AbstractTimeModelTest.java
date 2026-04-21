package edu.luc.etl.cs313.android.simplestopwatch.test.model;

import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.MAX_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.luc.etl.cs313.android.simplestopwatch.model.time.TimeModel;

/**
 * Testcase superclass for the time model abstraction.
 * This is a simple unit test of an object without dependencies.
 */
public abstract class AbstractTimeModelTest {

    private TimeModel model;

    protected void setModel(final TimeModel model) {
        this.model = model;
    }

    @Test
    public void testPreconditions() {
        assertEquals(0, model.getTime());
        assertTrue(model.isZero());
        assertFalse(model.isMax());
    }

    @Test
    public void testIncrementTimeOne() {
        model.incrementTime();
        assertEquals(1, model.getTime());
        assertFalse(model.isZero());
    }

    @Test
    public void testSetTimeWithinRange() {
        model.setTime(42);
        assertEquals(42, model.getTime());
        assertFalse(model.isZero());
        assertFalse(model.isMax());
    }

    @Test
    public void testSetTimeStopsAtMaximum() {
        model.setTime(MAX_TIME + 7);
        assertEquals(MAX_TIME, model.getTime());
        assertTrue(model.isMax());
    }

    @Test
    public void testIncrementTimeStopsAtMaximum() {
        for (var i = 0; i < MAX_TIME + 5; i++) {
            model.incrementTime();
        }
        assertEquals(MAX_TIME, model.getTime());
        assertTrue(model.isMax());
    }

    @Test
    public void testDecrementTimeOne() {
        model.incrementTime();
        model.incrementTime();
        model.decrementTime();
        assertEquals(1, model.getTime());
    }

    @Test
    public void testDecrementTimeStopsAtZero() {
        model.decrementTime();
        assertEquals(0, model.getTime());
        assertTrue(model.isZero());
    }

    @Test
    public void testResetTime() {
        model.incrementTime();
        model.incrementTime();
        model.resetTime();
        assertEquals(0, model.getTime());
        assertTrue(model.isZero());
    }
}
