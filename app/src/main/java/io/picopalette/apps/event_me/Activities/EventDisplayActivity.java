package io.picopalette.apps.event_me.Activities;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventDisplayActivity extends FragmentActivity implements OnMapReadyCallback {
    private Event eve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_display);
        eve = (Event) getIntent().getSerializableExtra("event");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        ImageView eImage = (ImageView) findViewById(R.id.eventPic);
        TextView eStatus = (TextView) findViewById(R.id.eventStatus);
        TextView eName = (TextView) findViewById(R.id.eventName);
        TextView ePlace = (TextView) findViewById(R.id.eventPlace);
        TextView eTime = (TextView) findViewById(R.id.eventTime);
        
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
        googleMap.addMarker(new MarkerOptions()).setPosition(new LatLng(lat,lon));

    }
}
