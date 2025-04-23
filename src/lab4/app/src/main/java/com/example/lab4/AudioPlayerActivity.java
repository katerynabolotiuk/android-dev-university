package com.example.lab4;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class AudioPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView textTitle, currentTimeTextView, totalTimeTextView;
    private Button buttonPlay, buttonPause, buttonStop;
    private Handler handler = new Handler();

    private Runnable updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        Uri audioUri = getIntent().getData();

        textTitle = findViewById(R.id.text_title);
        currentTimeTextView = findViewById(R.id.text_current_time);
        totalTimeTextView = findViewById(R.id.text_total_time);
        seekBar = findViewById(R.id.seek_bar);
        buttonPlay = findViewById(R.id.btn_play);
        buttonPause = findViewById(R.id.btn_pause);
        buttonStop = findViewById(R.id.btn_stop);

        File file = new File(audioUri.getPath());
        textTitle.setText(file.getName());

        mediaPlayer = MediaPlayer.create(this, audioUri);
        if (mediaPlayer == null) {
            textTitle.setText("Error loading audio");
            return;
        }

        int totalTime = mediaPlayer.getDuration();
        String totalTimeFormatted = formatTime(totalTime);
        totalTimeTextView.setText(totalTimeFormatted);

        seekBar.setMax(totalTime);

        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);

                    String currentTime = formatTime(currentPosition);
                    currentTimeTextView.setText(currentTime);

                    handler.postDelayed(this, 500);
                }
            }
        };

        handler.post(updateSeekbar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        buttonPlay.setOnClickListener(v -> mediaPlayer.start());
        buttonPause.setOnClickListener(v -> mediaPlayer.pause());
        buttonStop.setOnClickListener(v -> {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekbar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

