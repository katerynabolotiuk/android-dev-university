package com.example.lab3;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ViewDataActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private static final String FILE_NAME = "test.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        ArrayList<DataModel> data = readFile();
        DataAdapter adapter = new DataAdapter(data, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private ArrayList<DataModel> readFile() {
        ArrayList<DataModel> dataModels = new ArrayList<>();
        File file = new File(getFilesDir(), FILE_NAME);

        if (!file.exists()) {
            dataModels.add(new DataModel("File not found", "No data available"));
            return dataModels;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentQuestion = "";
            String currentAnswer = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Q: ")) {
                    currentQuestion = line.trim();
                }
                else if (line.startsWith("A: ")) {
                    currentAnswer = line.trim();

                    if (!currentQuestion.isEmpty() && !currentAnswer.isEmpty()) {
                        dataModels.add(new DataModel(currentAnswer, currentQuestion));
                        currentQuestion = "";
                        currentAnswer = "";
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            dataModels.add(new DataModel("Error reading file", "No data available"));
        }

        return dataModels;
    }
}