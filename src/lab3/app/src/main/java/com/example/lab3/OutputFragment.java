package com.example.lab3;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OutputFragment extends Fragment {

    private TextView textResult;

    public OutputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_output, container, false);

        textResult = view.findViewById(R.id.textResult);

        return view;
    }

    public void modifyText(String newText) {
        textResult.setText(newText);
    }
}