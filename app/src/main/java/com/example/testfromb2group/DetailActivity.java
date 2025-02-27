package com.example.testfromb2group;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Получаем данные из Intent
        int eventId = getIntent().getIntExtra("eventId", -1); // Значение по умолчанию -1
        String eventName = getIntent().getStringExtra("eventName");
        String eventDivisionType = getIntent().getStringExtra("eventDivisionType");
        int eventDel = getIntent().getIntExtra("eventDel", -1); // Значение по умолчанию -1

        // Находим TextViews в layout
        TextView textViewId = findViewById(R.id.textViewId);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewDivisionType = findViewById(R.id.textViewDivisionType);
        TextView textViewDel = findViewById(R.id.textViewDel);

        // Устанавливаем данные в TextViews
        textViewId.setText("ID: " + eventId);
        textViewName.setText("Name: " + eventName);
        textViewDivisionType.setText("Division Type: " + eventDivisionType);
        textViewDel.setText("Del: " + (eventDel == -1 ? "null" : eventDel)); // Обрабатываем значение по умолчанию
    }
}
