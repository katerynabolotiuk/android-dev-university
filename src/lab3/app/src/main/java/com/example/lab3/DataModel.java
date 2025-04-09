package com.example.lab3;

public class DataModel {
    String question;
    String answer;

    public DataModel(String answer, String question) {
        this.answer = answer;
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
