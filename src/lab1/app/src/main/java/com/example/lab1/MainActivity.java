package com.example.lab1;

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

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText questionEditText;
    private RadioGroup radioGroup;
    private Button buttonOk;
    private TextView textResult;


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

        initWidgets();
        setButtonOkClickListener();
    }

    private void initWidgets() {
        questionEditText = findViewById(R.id.questionEditText);
        radioGroup = findViewById(R.id.radioGroup);
        buttonOk = findViewById(R.id.buttonOk);
        textResult = findViewById(R.id.textResult);
    }

    private void setButtonOkClickListener() {
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionEditText.getText().toString().trim();
                if (question.isEmpty() || !question.endsWith("?")) {
                Toast.makeText(MainActivity.this, "Invalid question", Toast.LENGTH_LONG).show();
                return;
                }

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Select an answer", Toast.LENGTH_LONG).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                String selectedAnswer = selectedRadioButton.getText().toString();
                String finalText = "Q: " + question + "\nA: " + selectedAnswer;
                textResult.setText(finalText);
            }
        });
    }
}