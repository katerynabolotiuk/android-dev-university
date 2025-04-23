package com.example.lab4;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView videoView;
    private TextView currentTimeTextView, totalTimeTextView;
    private SeekBar seekBar;

    private Button btnPlay, btnPause, btnStop;
    private Handler handler = new Handler();
    private Runnable updateTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Uri videoUri = getIntent().getData();

        videoView = findViewById(R.id.video_view);
        btnPlay = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnStop = findViewById(R.id.btn_stop);
        currentTimeTextView = findViewById(R.id.text_current_time);
        totalTimeTextView = findViewById(R.id.text_total_time);
        seekBar = findViewById(R.id.seek_bar);


        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            float videoRatio = (float) mp.getVideoWidth() / mp.getVideoHeight();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int idealHeight = (int) (screenWidth / videoRatio);
            int maxAllowedHeight = screenHeight / 2;
            int finalHeight = Math.min(idealHeight, maxAllowedHeight);
            videoView.getLayoutParams().height = finalHeight;
            videoView.requestLayout();

            int duration = mp.getDuration();
            seekBar.setMax(duration);
            totalTimeTextView.setText(formatTime(duration));

            btnPlay.setOnClickListener(v -> videoView.start());
            btnPause.setOnClickListener(v -> videoView.pause());
            btnStop.setOnClickListener(v -> {
                videoView.pause();
                videoView.seekTo(0);
                seekBar.setProgress(0);
                currentTimeTextView.setText(formatTime(0));
            });

            startUpdateTimer();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startUpdateTimer() {
        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (videoView.isPlaying()) {
                    int currentPos = videoView.getCurrentPosition();
                    seekBar.setProgress(currentPos);
                    currentTimeTextView.setText(formatTime(currentPos));
                }
                handler.postDelayed(this, 500);
            }
        };
        handler.post(updateTimeTask);
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeTask);
        videoView.stopPlayback();
    }
}