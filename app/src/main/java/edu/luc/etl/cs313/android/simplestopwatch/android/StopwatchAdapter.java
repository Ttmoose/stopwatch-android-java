package edu.luc.etl.cs313.android.simplestopwatch.android;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;

import java.util.Locale;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.ConcreteStopwatchModelFacade;
import edu.luc.etl.cs313.android.simplestopwatch.model.StopwatchModelFacade;

/**
 * A thin adapter component for the stopwatch.
 *
 * @author laufer
 */
public class StopwatchAdapter extends Activity implements StopwatchModelListener {

    private StopwatchModelFacade model;

    private MediaPlayer alarmPlayer;

    private boolean stoppedState = true;

    protected void setModel(final StopwatchModelFacade model) {
        this.model = model;
    }

    @Override
    public void playBeep() {
        runOnUiThread(() -> {
            final ToneGenerator toneGenerator =
                    new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
            new Handler(Looper.getMainLooper()).postDelayed(toneGenerator::release, 300);
        });
    }

    @Override
    public void startAlarmSound() {
        runOnUiThread(() -> {
            if (alarmPlayer == null) {
                alarmPlayer = MediaPlayer.create(this, R.raw.app_src_main_res_raw_alarm_beep);
                if (alarmPlayer == null) {
                    return;
                }
                alarmPlayer.setLooping(true);
            }
            if (!alarmPlayer.isPlaying()) {
                alarmPlayer.start();
            }
        });
    }

    @Override
    public void stopAlarmSound() {
        runOnUiThread(() -> {
            if (alarmPlayer == null) {
                return;
            }
            if (alarmPlayer.isPlaying()) {
                alarmPlayer.stop();
            }
            alarmPlayer.release();
            alarmPlayer = null;
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setModel(new ConcreteStopwatchModelFacade());
        model.setModelListener(this);
        configureManualInput();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        model.start();
    }

    @Override
    protected void onStop() {
        stopAlarmSound();
        super.onStop();
    }

    /**
     * Updates the displayed time in the UI.
     *
     * @param time
     */
    public void onTimeUpdate(final int time) {
        runOnUiThread(() -> {
            final TextView tvS = findViewById(R.id.seconds);
            final var locale = Locale.getDefault();
            tvS.setText(String.format(locale, "%02d", time));
        });
    }

    /**
     * Updates the state name in the UI.
     *
     * @param stateId
     */
    public void onStateUpdate(final int stateId) {
        runOnUiThread(() -> {
            stoppedState = stateId == R.string.STOPPED;
            final TextView stateName = findViewById(R.id.stateName);
            final TextView stateHint = findViewById(R.id.stateHint);
            final Button actionButton = findViewById(R.id.onAction);
            stateName.setText(getString(stateId));
            stateHint.setText(getStateHint(stateId));
            actionButton.setText(getActionLabel(stateId));
            actionButton.setEnabled(true);
            updateManualInputState();
        });
    }

    private int getStateHint(final int stateId) {
        if (stateId == R.string.WAITING) {
            return R.string.state_hint_waiting;
        }
        if (stateId == R.string.ALARMING) {
            return R.string.state_hint_alarming;
        }
        if (stateId == R.string.RUNNING) {
            return R.string.state_hint_running;
        }
        return R.string.state_hint_stopped;
    }

    private int getActionLabel(final int stateId) {
        if (stateId == R.string.ALARMING) {
            return R.string.action_stop_alarm;
        }
        if (stateId == R.string.RUNNING) {
            return R.string.action_reset_time;
        }
        return R.string.action_add_time;
    }

    public void onPrimaryButton(final View view) {
        if (submitTypedTimeIfPresent()) {
            return;
        }
        model.onAction();
    }

    private void configureManualInput() {
        final EditText manualInput = findViewById(R.id.timerLabel);
        manualInput.setOnEditorActionListener((textView, actionId, event) -> {
            if (!isEnterAction(actionId, event)) {
                return false;
            }
            return submitTypedTimeIfPresent();
        });
        updateManualInputState();
    }

    private void updateManualInputState() {
        final EditText manualInput = findViewById(R.id.timerLabel);
        manualInput.setEnabled(stoppedState);
        manualInput.setFocusable(stoppedState);
        manualInput.setFocusableInTouchMode(stoppedState);
        manualInput.setCursorVisible(stoppedState);
        if (!stoppedState) {
            manualInput.getText().clear();
            manualInput.clearFocus();
        }
    }

    private boolean submitTypedTimeIfPresent() {
        if (!stoppedState) {
            return false;
        }
        final EditText manualInput = findViewById(R.id.timerLabel);
        final int typedTime = parseTypedTime(manualInput);
        if (typedTime <= 0) {
            return false;
        }
        model.onSetTime(typedTime);
        manualInput.getText().clear();
        manualInput.clearFocus();
        return true;
    }

    private int parseTypedTime(final EditText manualInput) {
        final String value = manualInput.getText().toString().trim();
        if (value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException ignored) {
            return 0;
        }
    }

    private boolean isEnterAction(final int actionId, final KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return true;
        }
        return actionId == EditorInfo.IME_NULL
                && event != null
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }
}
