package com.example.lab2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements InputFragment.OnFragmentCommunication {
    private InputFragment inputFragment;
    private OutputFragment outputFragment;


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

        inputFragment = (InputFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentInput);
        outputFragment = (OutputFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentOutput);
    }

    @Override
    public void onSuccess(String string) {
        outputFragment.modifyText(string);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}