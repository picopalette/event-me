package io.picopalette.apps.event_me;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;

import io.picopalette.apps.event_me.Datas.DateAndTime;
import io.picopalette.apps.event_me.Datas.MyEvent;


public class EventCreation extends AppCompatActivity implements View.OnClickListener, PlaceSelectionListener {

    private EditText Event_name,date,time,Event_type,Event_key;
    private DateAndTime dateAndTime;
    private String place_name;
    private Button complete;
    private DatabaseReference mDatabaseReference;
    private Switch mswitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        date = (EditText) findViewById(R.id.date_text);
        mswitch = (Switch) findViewById(R.id.eve_switch);
        time = (EditText) findViewById(R.id.time_text);
        Event_type = (EditText) findViewById(R.id.eve_type);
        Event_name = (EditText) findViewById(R.id.eve_name);
        Event_key = (EditText) findViewById(R.id.eve_keyword);
        complete = (Button) findViewById(R.id.add_event);
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        complete.setOnClickListener(this);
        dateAndTime = new DateAndTime();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_auto);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == date)
        {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreation.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dateAndTime.setYear(year);
                    dateAndTime.setMonth(month);
                    dateAndTime.setDayOfMonth(dayOfMonth);
                    date.setText(dateAndTime.getFormattedDate());
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
        if(v == time)
        {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreation.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    dateAndTime.setHourOfDay(hourOfDay);
                    dateAndTime.setMinute(minute);
                    time.setText(dateAndTime.getFormattedTime());
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        }
        if(v == complete)
        {
            if(!TextUtils.isEmpty(Event_name.getText().toString()) && !TextUtils.isEmpty(Event_type.getText().toString()) &&
                    !TextUtils.isEmpty(date.getText().toString()) && !TextUtils.isEmpty(time.getText().toString()) &&
                    !TextUtils.isEmpty(place_name) )
            {
                DatabaseReference EventReference = mDatabaseReference.child("Events");
                String my_keys = EventReference.push().getKey();
                Boolean mPrivate = mswitch.isChecked();
                MyEvent event = new MyEvent(Event_name.getText().toString(),Event_type.getText().toString(),place_name,date.getText().toString(),time.getText().toString(),mPrivate,my_keys);
                EventReference.child(my_keys).setValue(event);
                Toast.makeText(getBaseContext(),"Event Created Successfully",Toast.LENGTH_LONG).show();
                finish();

            }
            else
            {
                Toast.makeText(getBaseContext(),"Fill all Details",Toast.LENGTH_LONG).show();
            }
        }



    }

    @Override
    public void onPlaceSelected(Place place) {
        place_name = place.getName().toString();
        Toast.makeText(getBaseContext(),place_name,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getBaseContext(),status.toString(),Toast.LENGTH_LONG).show();
    }
}
