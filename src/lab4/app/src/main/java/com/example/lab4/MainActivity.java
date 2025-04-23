package com.example.lab4;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.net.Uri;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_AUDIO_REQUEST = 100;
    private static final int PICK_VIDEO_REQUEST = 101;
    private static final int REQUEST_WRITE_STORAGE = 124;
    private static final int REQUEST_PERMISSION_AUDIO = 125;
    private static final int REQUEST_PERMISSION_VIDEO = 126;

    private Button buttonSelectAudio, buttonSelectVideo, buttonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonSelectAudio = findViewById(R.id.button_select_audio);
        buttonSelectVideo = findViewById(R.id.button_select_video);
        buttonDownload = findViewById(R.id.button_download);

        buttonSelectAudio.setOnClickListener(v -> {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                openAudioPicker();
            else
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_AUDIO, "READ PERMISSION IS REQUIRED");
        });

        buttonSelectVideo.setOnClickListener(v -> {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                openVideoPicker();
            else
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_AUDIO, "READ PERMISSION IS REQUIRED");
        });

        buttonDownload.setOnClickListener(v -> {
            if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDownloadDialog();
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE, "WRITE PERMISSION IS REQUIRED");
            }
        });
    }

    boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(String permission, int requestCode, String rationaleMessage) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
            Toast.makeText(MainActivity.this, rationaleMessage, Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_PERMISSION_AUDIO:
                    openAudioPicker();
                    break;
                case REQUEST_PERMISSION_VIDEO:
                    openVideoPicker();
                    break;
                case REQUEST_WRITE_STORAGE:
                    showDownloadDialog();
                    break;
            }
        } else {
            Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_AUDIO_REQUEST);
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri mediaUri = data.getData();
            if (requestCode == PICK_AUDIO_REQUEST) {
                Intent intent = new Intent(this, AudioPlayerActivity.class);
                intent.setData(mediaUri);
                startActivity(intent);
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.setData(mediaUri);
                startActivity(intent);
            }
        }
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_download, null);
        final EditText input = dialogView.findViewById(R.id.url_input);

        builder.setView(dialogView)
                .setTitle("Download File")
                .setPositiveButton("Download", (dialog, which) -> {
                    String url = input.getText().toString();
                    if (!url.isEmpty()) {
                        new DownloadTask().execute(url);
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private class DownloadTask extends AsyncTask<String, Integer, File> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Downloading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                String fileName = url.getFile();
                fileName = fileName.substring(fileName.lastIndexOf('/') + 1);

                int fileLength = connection.getContentLength();

                InputStream input = connection.getInputStream();
                File outputFile = new File(getExternalFilesDir(null), fileName);
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return outputFile;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(File file) {
            progressDialog.dismiss();
            if (file != null) {
                Toast.makeText(MainActivity.this, "DOWNLOAD COMPLETE", Toast.LENGTH_SHORT).show();

                Uri mediaUri = Uri.fromFile(file);
                String fileName = file.getName().toLowerCase();

                if (fileName.endsWith(".mp3")) {
                    Intent intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
                    intent.setData(mediaUri);
                    startActivity(intent);
                } else if (fileName.endsWith(".mp4")) {
                    Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                    intent.setData(mediaUri);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Unsupported file format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "DOWNLOAD FAILED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}