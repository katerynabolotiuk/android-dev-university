package com.example.lab3;

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
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class InputFragment extends Fragment {
    private static final String FILE_NAME = "test.txt";
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

                        writeToFile(finalText);
                        
                        listener.onSuccess(finalText);
                    }
                }
            }
        });

        return view;
    }

    private void writeToFile(String text) {
        try {
            File file = new File(requireContext().getFilesDir(), FILE_NAME);
            FileWriter writer = new FileWriter(file, true);
            writer.append(text).append("\n\n");
            writer.close();
            listener.onMessage("Successfully saved data to file");
        } catch (IOException e) {
            e.printStackTrace();
            listener.onMessage("Failed to save data to file");
        }
    }

    private int getValidAnswer() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            listener.onMessage("Select an answer");
        }

        return selectedId;
    }

    private String getValidQuestion() {
        String input = questionEditText.getText().toString().trim();

        if (input.isEmpty() || !input.endsWith("?")) {
            listener.onMessage("Invalid question");
            return null;
        }

        return input;
    }

    public interface OnFragmentCommunication {
        public void onSuccess(String string);
        public void onMessage(String message);
    }
}