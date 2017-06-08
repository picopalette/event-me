package io.picopalette.apps.event_me.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.picopalette.apps.event_me.Adapters.FilterAdapter;
import io.picopalette.apps.event_me.Datas.DateAndTime;
import io.picopalette.apps.event_me.Datas.MyEvent;
import io.picopalette.apps.event_me.Datas.SimpleContact;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.ContactsCompletionView;


public class EventCreation extends AppCompatActivity implements PlaceSelectionListener, TokenCompleteTextView.TokenListener<SimpleContact>{


    private EditText Event_name,date,time,Event_type,Event_key;
    private ArrayList<SimpleContact> contacts;
    private FilterAdapter filterAdapter;
    private ContactsCompletionView autoCompleteTextView;
    private DateAndTime dateAndTime;
    private String place_name;
    private Button complete;
    private DatabaseReference mDatabaseReference;
    private Switch mitch;
    private int REQUEST_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    private ImageView Event_image;
    private String value = null ;
    private ProgressDialog progressDialog;
    private String url = "https://event-me-firebase.firebaseio.com/people.json";
    private String TAG = "testing";
    //private MultiAutoCompleteTextView automcomplete;
    private ArrayList<String> users;
    private AlertDialog.Builder diaog;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(EventCreation.this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("people");
        AlertDialog.Builder diaog = new AlertDialog.Builder(this).setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_custom, null);
        diaog.setView(dialogView);
        alertDialog = diaog.create();
        alertDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        int dialogWindowWidth = 600;
        int dialogWindowHeight = 400;
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        alertDialog.getWindow().setAttributes(layoutParams);
       /* automcomplete = (MultiAutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        automcomplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());*/
        date = (EditText) findViewById(R.id.date_text);
        mitch = (Switch) findViewById(R.id.eve_switch);
        time = (EditText) findViewById(R.id.time_text);
        Event_type = (EditText) findViewById(R.id.eve_type);
        Event_name = (EditText) findViewById(R.id.eve_name);
        Event_key = (EditText) findViewById(R.id.eve_keyword);
        Event_image = (ImageView) findViewById(R.id.event_image);
        complete = (Button) findViewById(R.id.add_event);
        autoCompleteTextView = (ContactsCompletionView) findViewById(R.id.autocomplete_textview);
        Log.d("test1","before fetchAndParse");
        fetchAndParsewithFirebase();
        filterAdapter = new FilterAdapter(this, R.layout.item_contact, contacts);
        Log.d("test1","after fetchAndParse");
        autoCompleteTextView.setTokenListener(EventCreation.this);
        autoCompleteTextView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

       /* btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<SimpleContact> tokens = autoCompleteTextView.getObjects();
                StringBuilder content = new StringBuilder();
                for (int i = 0; i < tokens.size(); i++) {
                    content.append(tokens.get(i)).append("; ");
                }
                inputContent.setText(String.format("You choose: %s", content.toString()));
            }
        });*/

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(Event_name.getText().toString()) && !TextUtils.isEmpty(Event_type.getText().toString()) &&
                        !TextUtils.isEmpty(date.getText().toString()) && !TextUtils.isEmpty(time.getText().toString()) &&
                        !TextUtils.isEmpty(place_name) )
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference EventRefUser = mDatabaseReference.child(Constants.users).child(EncodeString(user.getEmail()));
                    DatabaseReference EventReference = mDatabaseReference.child(Constants.events);
                    String my_keys = EventReference.push().getKey();
                    Boolean mPrivate = mitch.isChecked();
                    EventRefUser.child(Constants.events).child(my_keys).setValue(GetValue());
                    MyEvent event = new MyEvent(Event_name.getText().toString(),Event_type.getText().toString(),place_name,date.getText().toString(),time.getText().toString(),mPrivate,my_keys);
                    EventReference.child(my_keys).setValue(event);

                    Toast.makeText(getBaseContext(), R.string.success,Toast.LENGTH_LONG).show();
                    finish();

                }
                else
                {
                    Toast.makeText(getBaseContext(), R.string.fill_details,Toast.LENGTH_LONG).show();
                }

            }
        });
        dateAndTime = new DateAndTime();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_auto);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               PickImage();
            }
        });

    }

    private void fetchAndParsewithFirebase() {
        contacts = new ArrayList<>();


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("test1","inside Datasnapshot before initializing");

                Log.d("test1","insidedpsn after init");
                Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> e : td.entrySet()) {
                   // users.add(e.getKey());
                    Log.d("test1","in");
                    contacts.add(new SimpleContact(R.drawable.male, e.getKey(), (String) e.getValue()));

                }

                autoCompleteTextView.setAdapter(filterAdapter);

               /* ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (EventCreation.this, android.R.layout.select_dialog_item, users);*/
               /* automcomplete.setThreshold(2);
                automcomplete.setAdapter(adapter);*/

                Log.d("testing","success");
                alertDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                alertDialog.dismiss();
                Toast.makeText(EventCreation.this,"Netwok Error",Toast.LENGTH_LONG).show();
            }
        });



    }


    private void PickImage() {
        ImagePicker.create(this)
                .folderMode(true)
                .folderTitle(getString(R.string.my_images))
                .imageTitle(getString(R.string.select))
                .single()
                .showCamera(false)
                .imageDirectory(getString(R.string.imges_jpeg))
                .start(REQUEST_CODE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images =  data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            StringBuilder sb = new StringBuilder();
            for (int i = 0, l = images.size(); i < l; i++) {
                sb.append(images.get(i).getPath()).append("\n");
            }
            String uri = sb.toString();
            Event_image.setImageURI(Uri.parse(uri.trim()));
        }
    }

 /*   @Override
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference EventRefUser = mDatabaseReference.child(Constants.users).child(EncodeString(user.getEmail()));
                DatabaseReference EventReference = mDatabaseReference.child(Constants.events);
                String my_keys = EventReference.push().getKey();
                Boolean mPrivate = mitch.isChecked();
                EventRefUser.child(Constants.events).child(my_keys).setValue(GetValue());
                MyEvent event = new MyEvent(Event_name.getText().toString(),Event_type.getText().toString(),place_name,date.getText().toString(),time.getText().toString(),mPrivate,my_keys);
                EventReference.child(my_keys).setValue(event);

                Toast.makeText(getBaseContext(), R.string.success,Toast.LENGTH_LONG).show();
                finish();

            }
            else
            {
                Toast.makeText(getBaseContext(), R.string.fill_details,Toast.LENGTH_LONG).show();
            }
        }



    }*/

    private String GetValue() {
        return "ongoing";
    }

    private String EncodeString(String email) {
        return email.replace(".",Constants.dot);
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


    @Override
    public void onTokenAdded(SimpleContact token) {

    }

    @Override
    public void onTokenRemoved(SimpleContact token) {

    }


}

//For getting contents from that android-chip view

/*
    List<SimpleContact> tokens = autoCompleteTextView.getObjects();
    StringBuilder content = new StringBuilder();
                for (int i = 0; i < tokens.size(); i++) {
        content.append(tokens.get(i)).append("; ");
        }*/