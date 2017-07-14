package io.picopalette.apps.event_me.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
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
import android.widget.Switch;
import android.widget.TextView;

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
import com.google.api.client.util.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uber.sdk.android.core.UberButton;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.picopalette.apps.event_me.Adapters.ParticipantsAdapter;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class EventDisplayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Event eve;
    private String from = null;
    private CardView mTrackify,reqbuttoncard;
    private RideRequestButton uberButton;
    private SessionConfiguration uberConfig;
    private Button request;
    private TextView joinedeve;
    private String myemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        myemail = Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        eve = (Event) getIntent().getSerializableExtra("event");
        from = String.valueOf( getIntent().getExtras().get( "from" ) );
        Log.d("fromtezt",from.toString());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        final ImageView eImage = (ImageView) findViewById(R.id.eventPic);
        request = (Button) findViewById( R.id.joinevent );
        request.setVisibility( View.GONE );
        TextView eStatus = (TextView) findViewById(R.id.eventStatus);
        TextView eName = (TextView) findViewById(R.id.eventName);
        TextView ePlace = (TextView) findViewById(R.id.eventPlaceName);
        TextView eDate = (TextView) findViewById(R.id.eventDate);
        TextView eTime = (TextView) findViewById(R.id.eventTime);
        joinedeve = (TextView) findViewById( R.id.joinedtheeventext );
        joinedeve.setVisibility( View.GONE );
        RecyclerView participantsRecyclerView = (RecyclerView) findViewById(R.id.event_display_rec_view);
        AppCompatImageView eNavi = (AppCompatImageView) findViewById(R.id.navigation_btn);
        View mapCard = (View) findViewById(R.id.map_card);
        uberButton = (RideRequestButton) findViewById(R.id.uberRequestButton);
        reqbuttoncard = (CardView) findViewById( R.id.requestCardbutton );
//        reqbuttoncard.setVisibility( View.GONE );
        correctify();
        mTrackify = (CardView) findViewById(R.id.card3);
        Switch trackifySwitch = (Switch) findViewById(R.id.eve_switch2);
        mTrackify.setVisibility(View.GONE);



        trackifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mTrackify.setVisibility(View.VISIBLE);
                }
                else
                {
                    mTrackify.setVisibility(View.GONE);
                }
            }
        });

        eName.setText(eve.getName());
        getSupportActionBar().setTitle(eve.getName());
        ePlace.setText(eve.getPlace().getName());
        eTime.setText(eve.getDateAndTime().getFormattedTime());
        eDate.setText(eve.getDateAndTime().getFormattedDate());
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
        ParticipantsAdapter participantsAdapter = new ParticipantsAdapter(this, partiEmails);
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

        joinedeve.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child( Constants.events )
                        .child( eve.getId() )
                        .child(Constants.requests)
                        .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .setValue( null );
                request.setVisibility( View.VISIBLE );
                joinedeve.setVisibility( View.GONE );

            }
        } );
        request.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d( "insideOnclickrequest","hello" );
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
//                Log.d("timeresr", String.valueOf( seconds ) );

                FirebaseDatabase.getInstance().getReference()
                        .child( Constants.events )
                        .child( eve.getId() )
                        .child(Constants.requests)
                        .child( Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .setValue( currentDateandTime );
                joinedeve.setVisibility( View.VISIBLE );
                request.setVisibility( View.GONE );
            }
        } );
    }

    private void correctify() {
        final Boolean check = false;
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child( Constants.events )
                .child( eve.getId() )
                .child( Constants.requests );
        if(Objects.equals( from, "search" )){
            databaseReference.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Log.d("fishy2", String.valueOf( snapshot.getKey() ) +"  "+ myemail );
                        if(Objects.equals( String.valueOf( snapshot.getKey() ), myemail )){
                            joinedeve.setVisibility( View.VISIBLE );

                        }
                    }

                    Log.d("testings55", String.valueOf( joinedeve.getVisibility() ) );
                    if(joinedeve.getVisibility() != 0){
                        request.setVisibility( View.VISIBLE );
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            } );

        }

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
