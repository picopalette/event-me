package io.picopalette.apps.event_me.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tokenautocomplete.TokenCompleteTextView;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.picopalette.apps.event_me.Adapters.FilterAdapter;
import io.picopalette.apps.event_me.Models.DateAndTime;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.Models.Location;
import io.picopalette.apps.event_me.Models.SimpleContact;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.ContactsCompletionView;
import io.picopalette.apps.event_me.Utils.Utilities;


public class EventCreationActivity extends AppCompatActivity implements PlaceSelectionListener, TokenCompleteTextView.TokenListener<SimpleContact>{

    private EditText Event_name,date,time,endTime,Event_type,Event_key;
    private DateAndTime dateAndTime;
    private Location place;
    private Button complete;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mPeopleReference;
    private Switch mitch;
    private ImageView Event_image;
    private HashMap<String, Constants.UserStatus> participants;
    private Uri downloadUrl;
    private ProgressDialog progressDialog;
    private TextView mPrivateEvent;
    private Button mPar;
    private Event event;
    private PlaceAutocompleteFragment autocompleteFragment;
    private String my_key;
    private HashMap<String,Boolean> liveparticipants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mPeopleReference = mDatabaseReference.child(Constants.people);
        progressDialog = new ProgressDialog(EventCreationActivity.this);
        progressDialog.setTitle("Creating Event");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        date = (EditText) findViewById(R.id.date_text);
        mitch = (Switch) findViewById(R.id.eve_switch);
        time = (EditText) findViewById(R.id.time_text);
        endTime = (EditText) findViewById(R.id.end_time_text);
        Event_type = (EditText) findViewById(R.id.eve_type);
        Event_name = (EditText) findViewById(R.id.eve_name);
        Event_key = (EditText) findViewById(R.id.eve_description);
        Event_image = (ImageView) findViewById(R.id.event_image);
        complete = (Button) findViewById(R.id.add_event);
        mPrivateEvent = (TextView) findViewById(R.id.privateEventTV);
        mPar = (Button) findViewById(R.id.add_par);
        dateAndTime = new DateAndTime();
        participants = new HashMap<>();
        liveparticipants = new HashMap<>();
        mPar.setVisibility(View.GONE);

        mitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    mPrivateEvent.setTextColor(Color.parseColor("#094782"));
                    mPrivateEvent.setTypeface(null, Typeface.BOLD);
                }
                else
                {
                    mPrivateEvent.setTextColor(Color.parseColor("#666666"));
                    mPrivateEvent.setTypeface(null, Typeface.NORMAL);
                }

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreationActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateAndTime.setEndHourOfDay(hourOfDay);
                        dateAndTime.setEndMinute(minute);
                        endTime.setText(dateAndTime.getFormattedEndTime());
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
                        !TextUtils.isEmpty(Event_key.getText().toString()) && !TextUtils.isEmpty(endTime.getText().toString()) &&
                        (place!=null)  && (Event_image.getDrawable() != null))
                {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference eventRefUser = mDatabaseReference.child(Constants.users).child(Utilities.encodeEmail(user.getEmail()));
                    final DatabaseReference eventReference = mDatabaseReference.child(Constants.events);
                    if(intent.hasExtra( "event" )){
                        my_key = event.getId();

                    }
                    else
                    {
                        my_key = eventReference.push().getKey();
                    }
                    StorageReference everef = storageReference.child("images/"+my_key);
                    Log.d("sf", everef.toString());
                    Event_image.setDrawingCacheEnabled(true);
                    Event_image.buildDrawingCache();
                    Bitmap bitmap = Event_image.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = everef.putBytes(data);
                    progressDialog.show();
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getBaseContext(),"Cannot Upload",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            Intent intent1 = new Intent(EventCreationActivity.this, AddEventParticipantsActivity.class);
                            intent1.putExtra("eventId", my_key);
                            startActivity(intent1);
                            finish();
                        }
                    });
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = 100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setProgress((int) progress);
                            progressDialog.setMessage("Uploading your Event");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Upload Paused");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                        }
                    });
                    Boolean mPrivate = mitch.isChecked();
                    if(!intent.hasExtra("event")) {
                        eventRefUser.child(Constants.events).child(my_key).setValue(Constants.UserStatus.OWNER);
                        liveparticipants.put(Utilities.encodeEmail(user.getEmail()), false);
                        participants.put(Utilities.encodeEmail(user.getEmail()), Constants.UserStatus.OWNER);
                    }
                    Event event = new Event(Event_name.getText().toString(),Event_type.getText().toString(), Event_key.getText().toString(),place,dateAndTime,mPrivate,my_key, Constants.EventStatus.UPCOMING, participants,downloadUrl, Utilities.encodeEmail(user.getEmail()));
                    eventReference.child(my_key).setValue(event);
                    eventReference.child(my_key).child(Constants.livepart).setValue(liveparticipants);
                    Toast.makeText(getBaseContext(), R.string.success,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), R.string.fill_details,Toast.LENGTH_LONG).show();
                }



            }
        });
         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_auto);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Event's Destination");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               pickImage();
            }

            private void pickImage() {
//                    ImagePicker.create(EventCreationActivity.this)
//                            .folderMode(true)
//                            .folderTitle(getString(R.string.my_images))
//                            .imageTitle(getString(R.string.select))
//                            .single()
//                            .showCamera(true)
//                            .imageDirectory("Images/ *jpg")
//                            .start(REQUEST_CODE_PICKER);
                CropImage.activity()
                        .setAspectRatio(2,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(EventCreationActivity.this);

            }
        });

        if(intent.hasExtra( "event" )) {
            event = (Event) getIntent().getSerializableExtra("event");
            mPar.setVisibility(View.VISIBLE);
            complete.setText("Save Changes");
            mPar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),AddEventParticipantsActivity.class);
                    intent.putExtra("eventId", event.getId());
                    intent.putExtra("eventName", event.getName());
                    startActivity(intent);
                }
            });
            seteverything();

        }


    }

    private void seteverything() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images/"+event.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(Event_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        Event_name.setText(event.getName() );
        Event_type.setText( event.getType() );
        Event_key.setText(event.getKeyword());
        date.setText( event.getDateAndTime().getFormattedDate() );
        time.setText( event.getDateAndTime().getFormattedTime() );
        endTime.setText( event.getDateAndTime().getFormattedEndTime() );
        autocompleteFragment.setText( event.getPlace().getName() );
        dateAndTime = event.getDateAndTime();
        place = event.getPlace();
        participants = event.getParticipants();
        if(event.getPrivate())
        {
            mitch.setChecked( true );
        }
        else
            mitch.setChecked( false );



    }




//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
//            images =  data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0, l = images.size(); i < l; i++) {
//                sb.append(images.get(i).getPath()).append("\n");
//            }
//            String uri = sb.toString();
//            Event_image.setImageURI(Uri.parse(uri.trim()));
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Event_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("someerror",error.toString());
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        this.place = new Location(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
        String place_name = place.getName().toString();
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

/* List<SimpleContact> tokens = autoCompleteTextView.getObjects();
   StringBuilder content = new StringBuilder();
   for (int i = 0; i < tokens.size(); i++) {
        content.append(tokens.get(i)).append("; ");
      }*/