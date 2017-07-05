package io.picopalette.apps.event_me.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uber.sdk.android.core.UberButton;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventDisplayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Event eve;
    private CardView mTrackify;
    private RideRequestButton uberButton;
    private SessionConfiguration uberConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        eve = (Event) getIntent().getSerializableExtra("event");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        final ImageView eImage = (ImageView) findViewById(R.id.eventPic);
        TextView eStatus = (TextView) findViewById(R.id.eventStatus);
        TextView eName = (TextView) findViewById(R.id.eventName);
        TextView ePlace = (TextView) findViewById(R.id.eventDate);
        TextView eTime = (TextView) findViewById(R.id.eventTime);
        uberButton = (RideRequestButton) findViewById(R.id.uberRequestButton);
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
        ePlace.setText(eve.getPlace().getName());
        eTime.setText(eve.getDateAndTime().getFormattedTime());
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
