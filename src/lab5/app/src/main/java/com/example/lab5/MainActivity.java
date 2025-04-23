package com.example.lab5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity {

    private EditText stepGoalInput, timeLimitInput, strideLengthInput;
    private Button startTrackingButton;

    double strideLength = 0.76;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepGoalInput = findViewById(R.id.step_goal_input);
        timeLimitInput = findViewById(R.id.time_limit_input);
        strideLengthInput = findViewById(R.id.stride_length_input);
        startTrackingButton = findViewById(R.id.start_tracking_button);

        startTrackingButton.setOnClickListener(v -> {
            String stepGoalStr = stepGoalInput.getText().toString().trim();
            String timeLimitStr = timeLimitInput.getText().toString().trim();

            if (TextUtils.isEmpty(stepGoalStr)) {
                Toast.makeText(this, "Please enter a step goal", Toast.LENGTH_SHORT).show();
                return;
            }

            int stepGoal = Integer.parseInt(stepGoalStr);
            long timeLimit = -1;

            if (!TextUtils.isEmpty(timeLimitStr)) {
                try {
                    timeLimit = Long.parseLong(timeLimitStr) * 60;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String strideStr = strideLengthInput.getText().toString();
            if (!TextUtils.isEmpty(strideStr)) {
                try {
                    strideLength = Double.parseDouble(strideStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid stride length. Using default.", Toast.LENGTH_SHORT).show();
                }
            }

            Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
            intent.putExtra("STEP_GOAL", stepGoal);
            intent.putExtra("TIME_LIMIT", timeLimit);
            intent.putExtra("STRIDE_LENGTH", strideLength);
            startActivity(intent);
        });
    }
}

