package com.example.lab5;

import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import android.app.AlertDialog;
import android.os.VibrationEffect;
import android.os.Vibrator;
import java.util.Locale;

public class TrackerActivity extends ComponentActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private int stepGoal;
    private int currentSteps = 0;
    private long timeLimitSeconds;
    private double strideLength;
    private long startTime = 0;

    private boolean isRunning = false;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private TextView counterText, goalText, distanceText, elapsedText, remainingText;
    private ProgressBar progressBar;
    private Button startButton, pauseButton, continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        counterText = findViewById(R.id.counter_text);
        goalText = findViewById(R.id.goal_text);
        distanceText = findViewById(R.id.distance_text);
        elapsedText = findViewById(R.id.elapsed_text);
        remainingText = findViewById(R.id.remaining_text);
        progressBar = findViewById(R.id.progress_bar);
        startButton = findViewById(R.id.start_button);
        pauseButton = findViewById(R.id.pause_button);
        continueButton = findViewById(R.id.continue_button);

        stepGoal = getIntent().getIntExtra("STEP_GOAL", 0);
        timeLimitSeconds = getIntent().getLongExtra("TIME_LIMIT", -1);
        strideLength = getIntent().getDoubleExtra("STRIDE_LENGTH", 0.76);

        if (timeLimitSeconds > 0) {
            remainingText.setText(String.format(Locale.getDefault(),
                    "%02d:%02d", timeLimitSeconds / 60, timeLimitSeconds % 60));
        } else {
            remainingText.setText("∞");
        }

        goalText.setText(String.valueOf(stepGoal));
        progressBar.setMax(stepGoal);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        } else {
            initStepDetector();
        }

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                startTracking();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTracking();
            }
        });

        continueButton.setOnClickListener(v -> {
            if (!isRunning) {
                continueTracking();
            }
        });

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    updateTimerUI(elapsed);

                    if (timeLimitSeconds > 0 && elapsed >= timeLimitSeconds) {
                        pauseTracking();

                        if (currentSteps < stepGoal) {
                            progressBar.getProgressDrawable().setColorFilter(
                                    getResources().getColor(R.color.red),
                                    android.graphics.PorterDuff.Mode.SRC_IN);

                            new AlertDialog.Builder(TrackerActivity.this)
                                    .setTitle("Time's Up!")
                                    .setMessage("You didn't reach your goal in time.")
                                    .setPositiveButton("OK", null)
                                    .show();

                            continueButton.setEnabled(false);
                        }
                    }

                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startTracking() {
        isRunning = true;
        startTime = System.currentTimeMillis();
        timerHandler.post(timerRunnable);

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void pauseTracking() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        continueButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void continueTracking() {
        isRunning = true;
        startTime = System.currentTimeMillis() - getElapsedSeconds() * 1000;
        timerHandler.post(timerRunnable);

        pauseButton.setEnabled(true);
        continueButton.setEnabled(false);
    }

    private long getElapsedSeconds() {
        String[] timeParts = elapsedText.getText().toString().split(":");
        return Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
    }

    private void updateTimerUI(long elapsed) {
        elapsedText.setText(String.format(Locale.getDefault(),
                "%02d:%02d", elapsed / 60, elapsed % 60));

        if (timeLimitSeconds > 0) {
            long remaining = timeLimitSeconds - elapsed;
            if (remaining < 0) remaining = 0;
            remainingText.setText(String.format(Locale.getDefault(),
                    "%02d:%02d", remaining / 60, remaining % 60));
        } else {
            remainingText.setText("∞");
        }
    }

    private void initStepDetector() {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && isRunning) {
            currentSteps++;
            counterText.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps);

            double distance = currentSteps * strideLength / 1000.0;
            distanceText.setText(String.format(Locale.getDefault(), "%.2f km", distance));

            if (currentSteps >= stepGoal) {
                onGoalReached();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initStepDetector();
        }
    }

    private void onGoalReached() {
        pauseTracking();
        pauseButton.setEnabled(false);
        continueButton.setEnabled(false);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Goal Reached!")
                .setMessage("Congratulations! You've reached your step goal.")
                .setPositiveButton("OK", null)
                .show();
    }
}