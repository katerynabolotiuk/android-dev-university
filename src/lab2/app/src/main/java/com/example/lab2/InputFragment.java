package com.example.lab2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;


public class InputFragment extends Fragment {
    private TextInputEditText questionEditText;
    private RadioGroup radioGroup;
    private Button buttonOk;
    private OnFragmentCommunication listener;

    public InputFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (OnFragmentCommunication) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        questionEditText = view.findViewById(R.id.questionEditText);
        radioGroup = view.findViewById(R.id.radioGroup);
        buttonOk = view.findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = getValidQuestion();

                if (question != null) {
                    int selectedId = getValidAnswer();

                    if (selectedId != -1) {
                        RadioButton selectedRadioButton = view.findViewById(selectedId);
                        String selectedAnswer = selectedRadioButton.getText().toString();
                        String finalText = "Q: " + question + "\nA: " + selectedAnswer;

                        listener.onSuccess(finalText);
                    }
                }
            }
        });

        return view;
    }

    private int getValidAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            listener.onError("Select an answer");
        }

        return selectedId;
    }

    private String getValidQuestion() {
        String input = questionEditText.getText().toString().trim();

        if (input.isEmpty() || !input.endsWith("?")) {
            listener.onError("Invalid question");
            return null;
        }

        return input;
    }

    public interface OnFragmentCommunication {
        public void onSuccess(String string);
        public void onError(String message);
    }
}