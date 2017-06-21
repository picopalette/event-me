package io.picopalette.apps.event_me.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventDisplayActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        event = (Event) getIntent().getSerializableExtra("event");

    }
}
