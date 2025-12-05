package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;

import java.util.ArrayList;
import java.util.List;

public class TEventActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private TEventAdapter adapter;
    private List<TEventModel> eventList;

    private ImageButton btnAddEventTop, btnBackEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tevent);

        btnAddEventTop = findViewById(R.id.btnAddEventTop);
        btnBackEvent = findViewById(R.id.btnBackEvent);

        btnBackEvent.setOnClickListener(v -> finish());

        btnAddEventTop.setOnClickListener(v ->
                startActivity(new Intent(this, TCreateNewEventActivity.class)));

        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // Dummy Events
        eventList = new ArrayList<>();
        eventList.add(new TEventModel("1", "Annual Day", "25 Feb 2025", "Cultural programs, awards & performances."));
        eventList.add(new TEventModel("2", "Science Exhibition", "12 March 2025", "Students will showcase science models."));
        eventList.add(new TEventModel("3", "Sports Day", "5 April 2025", "Athletics, games & prizes."));
        eventList.add(new TEventModel("4", "Parent Meeting", "14 April 2025", "Parent–Teacher meeting."));

        adapter = new TEventAdapter(eventList,
                item -> Toast.makeText(this,
                        "Clicked: " + item.getTitle(),
                        Toast.LENGTH_SHORT).show());

        rvEvents.setAdapter(adapter);
    }
}
