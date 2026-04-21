package edu.luc.etl.cs313.android.simplestopwatch.android;

import android.app.Activity;
import android.media.AudioManager; // for managing audio streams
import android.media.MediaPlayer; // for playing the bundled alarm sound
import android.media.ToneGenerator;  // for generating simple tones for the beep
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import edu.luc.etl.cs313.android.simplestopwatch.R;
import edu.luc.etl.cs313.android.simplestopwatch.common.Constants;
import edu.luc.etl.cs313.android.simplestopwatch.common.StopwatchModelListener;
import edu.luc.etl.cs313.android.simplestopwatch.model.ConcreteStopwatchModelFacade;
import edu.luc.etl.cs313.android.simplestopwatch.model.StopwatchModelFacade;

/**
 * A thin adapter component for the stopwatch.
 *
 * @author laufer
 */
public class StopwatchAdapter extends Activity implements StopwatchModelListener {

    private static String TAG = "stopwatch-android-activity";

    /**
     * The state-based dynamic model.
     */
    private StopwatchModelFacade model;
    private volatile int currentStateId = R.string.STOPPED;

    protected void setModel(final StopwatchModelFacade model) {
        this.model = model;
    }

    /** Plays the bundled alarm sound. */
    public void playDefaultNotification(){
        final MediaPlayer mediaPlayer =
                MediaPlayer.create(this, R.raw.app_src_main_res_raw_alarm_beep);
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    /** plays the beep sound that happens before decrementing */
    public void playBeep(){
        // ToneGenerator instance with specific stream type & volume
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        // play a short beep tone
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
        // release ToneGenerator after a delay to ensure the tone completes
        new Handler(Looper.getMainLooper()).postDelayed(toneGenerator::release, 200);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inject dependency on view so this adapter receives UI events
        setContentView(R.layout.activity_main);
        // inject dependency on model into this so model receives UI events
        this.setModel(new ConcreteStopwatchModelFacade());
        // inject dependency on this into model to register for UI updates
        model.setModelListener(this);
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

    // TODO remaining lifecycle methods

    /**
     * Updates the seconds and minutes in the UI.
     * @param time
     */
    public void onTimeUpdate(final int time) {
        // UI adapter responsibility to schedule incoming events on UI thread
        runOnUiThread(() -> {
            final TextView tvS = findViewById(R.id.seconds);
            final var locale = Locale.getDefault();
            tvS.setText(String.format(locale, "%02d", time));
        });
    }

    /**
     * Updates the state name in the UI.
     * @param stateId
     */
    public void onStateUpdate(final int stateId) {
        currentStateId = stateId;
        // UI adapter responsibility to schedule incoming events on UI thread
        runOnUiThread(() -> {
            final TextView stateName = findViewById(R.id.stateName);
            final TextView stateHint = findViewById(R.id.stateHint);
            final Button actionButton = findViewById(R.id.onAction);
            stateName.setText(getString(stateId));
            stateHint.setText(getStateHint(stateId));
            actionButton.setText(getActionLabel(stateId));
            actionButton.setEnabled(isPrimaryButtonEnabled(stateId));
        });
    }

    private boolean isPrimaryButtonEnabled(final int stateId) {
        return stateId == R.string.STOPPED || stateId == R.string.ALARMING;
    }

    private int getStateHint(final int stateId) {
        if (stateId == R.string.INCREMENTING) {
            return R.string.state_hint_incrementing;
        }
        if (stateId == R.string.DECREMENTING) {
            return R.string.state_hint_decrementing;
        }
        if (stateId == R.string.ALARMING) {
            return R.string.state_hint_alarming;
        }
        if (stateId == R.string.RUNNING) {
            return R.string.state_hint_running;
        }
        if (stateId == R.string.LAP_RUNNING) {
            return R.string.state_hint_lap_running;
        }
        if (stateId == R.string.LAP_STOPPED) {
            return R.string.state_hint_lap_stopped;
        }
        return R.string.state_hint_stopped;
    }

    private int getActionLabel(final int stateId) {
        if (stateId == R.string.INCREMENTING) {
            return R.string.activate_more;
        }
        if (stateId == R.string.DECREMENTING) {
            return R.string.activate_cancel;
        }
        if (stateId == R.string.ALARMING) {
            return R.string.activate_acknowledge;
        }
        if (stateId == R.string.RUNNING
                || stateId == R.string.LAP_RUNNING
                || stateId == R.string.LAP_STOPPED) {
            return R.string.activate_disabled;
        }
        return R.string.activate_add;
    }

    // forward event listener methods to the model
    public void onPrimaryButton(final View view) {
        if (currentStateId == R.string.STOPPED) {
            model.onStartStop();
            return;
        }
        if (currentStateId == R.string.ALARMING) {
            model.onAction();
        }
    }

    public void onAction(final View view) {
        model.onAction();
    }

    public void onLapReset(final View view)  {
        model.onLapReset();
    }

    public void onStartStop(View view) {
        model.onStartStop();
    }

    public void onDecrement(final View view) {model.onDecrement();}
}
