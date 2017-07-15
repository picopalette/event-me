package io.picopalette.apps.event_me.Activities;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.picopalette.apps.event_me.Adapters.ParticipantsAdapter;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Services.LocationData;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class EventDisplayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Event eve;
    private String from = null;
    private CardView mTrackify,reqbuttoncard;
    private RideRequestButton uberButton;
    private SessionConfiguration uberConfig;
    private String myemail;
    private String islive;
    private Switch trackifySwitch;
    private LocationManager locationManager;
    private Button edit,delete,join,leave;
    private LinearLayout linearLayout;
    private TextView mShareView;
    private TextView mDescTextView;
    private TextView mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_event_display);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myemail = Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        eve = (Event) getIntent().getSerializableExtra("event");
        from = String.valueOf( getIntent().getExtras().get( "from" ) );
        Log.d("fromtezt",from.toString());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        linearLayout = (LinearLayout) findViewById(R.id.tobehiddenn);
//        linearLayout.setVisibility(View.GONE);
        edit =(Button) findViewById(R.id.editEvent);
        edit.setVisibility(View.GONE);
        delete = (Button) findViewById(R.id.deleteEvent);
        delete.setVisibility(View.GONE);
        join = (Button) findViewById(R.id.joinEvent);
        join.setVisibility(View.GONE);
        leave = (Button) findViewById(R.id.leaveEvent);
        leave.setVisibility(View.VISIBLE);
        final ImageView eImage = (ImageView) findViewById(R.id.eventPic);
        TextView eStatus = (TextView) findViewById(R.id.eventStatus);
        TextView eName = (TextView) findViewById(R.id.eventName);
        TextView ePlace = (TextView) findViewById(R.id.eventPlaceName);
        TextView eDate = (TextView) findViewById(R.id.eventDate);
        TextView eTime = (TextView) findViewById(R.id.eventTime);
        mShareView = (TextView) findViewById(R.id.shareView);
        mEndTime = (TextView) findViewById(R.id.endTimeTextView);
        mDescTextView = (TextView) findViewById(R.id.descTextView);
        RecyclerView participantsRecyclerView = (RecyclerView) findViewById(R.id.event_display_rec_view);
        AppCompatImageView eNavi = (AppCompatImageView) findViewById(R.id.navigation_btn);
        View mapCard = (View) findViewById(R.id.map_card);
        uberButton = (RideRequestButton) findViewById(R.id.uberRequestButton);
        mTrackify = (CardView) findViewById(R.id.card3);
        trackifySwitch = (Switch) findViewById(R.id.eve_switch2);
        mTrackify.setVisibility(View.GONE);
        correctify();

        mTrackify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDisplayActivity.this,LiveShare.class);
                intent.putExtra("lat",eve.getPlace().getLat());
                intent.putExtra("lon",eve.getPlace().getLon());
                intent.putExtra("uid",eve.getId());
                startActivity(intent);

            }
        });





        trackifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                    mShareView.setTextColor(Color.parseColor("#094782"));
                    mShareView.setTypeface(null, Typeface.BOLD);
                    FirebaseDatabase.getInstance().getReference().child(Constants.events)
                            .child(eve.getId())
                            .child(Constants.livepart)
                            .child(myemail)
                            .setValue(true);
                    if(isNetworkAvailable() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        if (isMyServiceRunning(LocationData.class)) {
                            mTrackify.setVisibility(View.VISIBLE);
                        }
                    }
//                        else
//                        {
//                            if(!permission_check())
//                            {
//                                Intent  i = new Intent(getApplicationContext(), LocationData.class);
//                                startService(i);
//                            }
//                            if(isMyServiceRunning(LocationData.class)){
//                                FirebaseDatabase.getInstance().getReference().child(Constants.events)
//                                        .child(eve.getId()).child(Constants.livepart)
//                                        .child(myemail).setValue(true);
//                                mTrackify.setVisibility(View.VISIBLE);
//                                trackifySwitch.setChecked(true);
//
//                            }
//                            else {
//                                mTrackify.setVisibility(View.GONE);
//                                trackifySwitch.setChecked(false);
//                            }
//
//                        }
//                    }
//                    else
//                    {
//                        Toast.makeText(getApplicationContext(),"No Network",Toast.LENGTH_SHORT).show();
//                        trackifySwitch.setChecked(false);
//                    }
                }
                else
                {
//                    Boolean needtoOff = false;
//                    FirebaseDatabase.getInstance().getReference().child(Constants.events).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for(DataSnapshot snapshot: dataSnapshot.getChildren())
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

//                    FirebaseDatabase.getInstance().getReference().child(Constants.events).child(eve.getId()).child(Constants.livepart).child(myemail).setValue(false);
//                    Intent  i = new Intent(getApplicationContext(), LocationData.class);
//                    stopService(i);
                    mShareView.setTextColor(Color.parseColor("#666666"));
                    mShareView.setTypeface(null, Typeface.NORMAL);
                    mTrackify.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference().child(Constants.events).child(eve.getId()).child(Constants.livepart).child(myemail).setValue(false);
                    trackifySwitch.setChecked(false);


                }
            }
        });

        eName.setText(eve.getName());
        getSupportActionBar().setTitle(eve.getName());
        ePlace.setText(eve.getPlace().getName());
        eTime.setText(eve.getDateAndTime().getFormattedTime());
        eDate.setText(eve.getDateAndTime().getFormattedDate());
        eStatus.setText(eve.getStatus().toString());
        mDescTextView.setText(eve.getKeyword());
        mEndTime.setText(eve.getDateAndTime().getFormattedEndTime());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images/"+eve.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(eImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        switch (eve.getStatus()) {
            case UPCOMING:
                eStatus.setText("Upcoming Event");
                eStatus.setBackgroundResource(R.color.yellowStatus);
                break;
            case ONGOING:
                eStatus.setText("Ongoing Event");
                eStatus.setBackgroundResource(R.color.ongoing_background);
                break;
            case ENDED:
                eStatus.setText("Ended");
                eStatus.setBackgroundResource(R.color.ended_red);
                break;
        }

        ArrayList<String> partiEmails = new ArrayList<>();
        for(String email : eve.getParticipants().keySet())
            partiEmails.add(email);
        Log.d("userEmails", partiEmails.toString());

        ArrayList<Constants.UserStatus> partstatus = new ArrayList<>();
        for(Constants.UserStatus status: eve.getParticipants().values())
            partstatus.add(status);

        ParticipantsAdapter participantsAdapter = new ParticipantsAdapter(this, partiEmails,partstatus);
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        participantsRecyclerView.setAdapter(participantsAdapter);

        uberConfig = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("5OmjOvLDwGdW4Uytw78StTo4oLRMWa40")
                // required for enhanced button features
                .setServerToken("c0Ziye1-rVCAb3RqpnVIP6ZIg1-jaKQY55BVuKr9")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(uberConfig);

        RideParameters rideParams = new RideParameters.Builder()
                .setPickupToMyLocation()
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location.
                .setDropoffLocation(eve.getPlace().getLat(), eve.getPlace().getLon(), eve.getPlace().getName(), eve.getName())
                .build();
// set parameters for the RideRequestButton instance
        uberButton.setRideParameters(rideParams);

        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+eve.getPlace().getLat()+","+eve.getPlace().getLon());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        eNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+eve.getPlace().getLat()+","+eve.getPlace().getLon()+"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDisplayActivity.this, EventCreationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
                intent.putExtra("event",eve);
                startActivity(intent);

            }
        });


        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child( Constants.events )
                        .child( eve.getId() )
                        .child(Constants.participants)
                        .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .setValue(Constants.UserStatus.GOING);

                FirebaseDatabase.getInstance().getReference()
                        .child(Constants.events)
                        .child(eve.getId())
                        .child(Constants.livepart)
                        .child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .setValue(false);

                FirebaseDatabase.getInstance().getReference()
                        .child(Constants.users)
                        .child(myemail)
                        .child(Constants.events)
                        .child( eve.getId()).setValue(Constants.UserStatus.GOING);

                join.setVisibility(View.GONE);
                leave.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventDisplayActivity.this);
                alertDialogBuilder.setMessage("Are you sure");
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference()
                                .child( Constants.events )
                                .child( eve.getId() )
                                .child(Constants.participants)
                                .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                                .setValue(null);
                        FirebaseDatabase.getInstance().getReference()
                                .child( Constants.events )
                                .child( eve.getId() )
                                .child(Constants.livepart)
                                .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                                .setValue(null);
                        FirebaseDatabase.getInstance().getReference()
                                .child(Constants.users)
                                .child(myemail)
                                .child(Constants.events)
                                .child( eve.getId()).setValue(null);
                        finish();


                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.show();


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventDisplayActivity.this);
                                          alertDialogBuilder.setMessage("Do you want to delete this event");
                                          alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {
                                                  for (String email : eve.getParticipants().keySet()) {
                                                      Log.d("myemailsss", email);
                                                      FirebaseDatabase.getInstance().getReference()
                                                              .child(Constants.users)
                                                              .child(email)
                                                              .child(Constants.events).child(eve.getId()).setValue(null);
                                                      FirebaseDatabase.getInstance().getReference().child(Constants.events)
                                                              .child(eve.getId())
                                                              .setValue(null);
                                                      finish();
                                                  }

                                              }
                                          });
                                          alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {

                                              }
                                          });
                                          alertDialogBuilder.show();

                                      }
                                  });

//        editButton.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(EventDisplayActivity.this, EventCreationActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
//                intent.putExtra("event",eve);
//                startActivity(intent);
//            }
//        } );
//        deletebtn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventDisplayActivity.this);
//                alertDialogBuilder.setMessage( "Do you want to delete this event" );
//                alertDialogBuilder.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        for(String email : eve.getParticipants().keySet()) {
//                            Log.d("myemailsss",email);
//                           FirebaseDatabase.getInstance().getReference()
//                                   .child( Constants.users )
//                                   .child( email )
//                                   .child( Constants.events ).child( eve.getId() ).setValue( "DELETED" );
//
//                            FirebaseDatabase.getInstance().getReference().child( Constants.events )
//                                    .child( eve.getId() )
//                                    .setValue( null );
//
//                            finish();
//                        }
//
//                    }
//                } );
//                alertDialogBuilder.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                } );
//                alertDialogBuilder.show();

//            }
//        } );

//        joinedeve.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseDatabase.getInstance().getReference()
//                        .child( Constants.events )
//                        .child( eve.getId() )
//                        .child(Constants.participants)
//                        .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
//                        .setValue(null);
//                FirebaseDatabase.getInstance().getReference()
//                        .child(Constants.users)
//                        .child(myemail)
//                        .child(Constants.events)
//                        .child( eve.getId()).setValue(Constants.UserStatus.LEFT);
//                request.setVisibility( View.VISIBLE );
//                joinedeve.setVisibility( View.GONE );

//            }
//        } );
//        request.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d( "insideOnclickrequest","hello" );
//
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child( Constants.events )
//                        .child( eve.getId() )
//                        .child(Constants.participants)
//                        .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
//                        .setValue(Constants.UserStatus.GOING);
//                FirebaseDatabase.getInstance().getReference()
//                        .child(Constants.users)
//                        .child(myemail)
//                        .child(Constants.events)
//                        .child( eve.getId()).setValue(Constants.UserStatus.GOING);
//                joinedeve.setVisibility( View.VISIBLE );
//                request.setVisibility( View.GONE );
//            }
//        } );
    }

    private boolean permission_check() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 69);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 69){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(EventDisplayActivity.this,"Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                permission_check();
            }
        }
    }

    private boolean isMyServiceRunning(Class<LocationData> locationDataClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (locationDataClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void correctify() {

        if(Objects.equals( from, "search" )){
//            request.setVisibility(View.VISIBLE);
            join.setVisibility(View.VISIBLE);
            leave.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }
        else {
            if (Objects.equals(eve.getOwner(), myemail)) {
                Log.d("insideexpected", "hello");
//                editButton.setVisibility(View.VISIBLE);
//                deletebtn.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                join.setVisibility(View.GONE);
                leave.setVisibility(View.GONE);


            }
            else

            {
                leave.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                join.setVisibility(View.GONE);

//                joinedeve.setVisibility(View.VISIBLE);
            }
        }
        Log.d("myemailtesting", eve.getOwner()+ " "+ myemail);

        FirebaseDatabase.getInstance().getReference().child(Constants.events).child(eve.getId()).child(Constants.livepart).child(myemail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("fuck android", String.valueOf(dataSnapshot.getValue()));
                if (dataSnapshot.getValue() != null) {
                    if (Objects.equals(dataSnapshot.getValue().toString(), "true")) {
                        trackifySwitch.setChecked(true);
                    } else
                        trackifySwitch.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_display,menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(eve.getPlace().getLat(),eve.getPlace().getLon()))
                .zoom(17)
                .bearing(0)
                .tilt(80)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(eve.getPlace().getLat(),eve.getPlace().getLon())));
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.chatIcon)
        {
            Intent intent = new Intent(EventDisplayActivity.this,MessagingActivity.class);
            intent.putExtra("id",eve.getId());
            startActivity(intent);

        }

        return true;
    }
}
