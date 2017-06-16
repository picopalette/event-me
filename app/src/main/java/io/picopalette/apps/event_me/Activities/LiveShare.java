package io.picopalette.apps.event_me.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.picopalette.apps.event_me.Models.SimpleContact;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Services.LocationData;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class LiveShare extends FragmentActivity  implements OnMapReadyCallback{

    private BroadcastReceiver broadcastReceiver;
    private Double lat, lon;
    private DatabaseReference userdata, livedata;
    private FirebaseUser user;
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveshare);
        userdata = FirebaseDatabase.getInstance().getReference().child("users");

        user = FirebaseAuth.getInstance().getCurrentUser();
        key = (String) getIntent().getExtras().get("uid");
        livedata = FirebaseDatabase.getInstance().getReference("events").child(key).child("participants");
        lat = (Double) getIntent().getExtras().get("lat");
        lon = (Double) getIntent().getExtras().get("lon");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(!permission_check())
        {
            Intent  i = new Intent(getApplicationContext(), LocationData.class);
            startService(i);
        }

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
                Toast.makeText(LiveShare.this,"Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                permission_check();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_data"));
    }

    @Override
    public void onBackPressed() {
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
        Intent i = new Intent(getApplicationContext(), LocationData.class);
        stopService(i);
        userdata.child(Utilities.encodeEmail(user.getEmail())).child("live").setValue("off");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userdata.child(Utilities.encodeEmail(user.getEmail())).child("live").setValue("off");
        Intent i = new Intent(getApplicationContext(), LocationData.class);
        stopService(i);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        livedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
                for (final Map.Entry<String, Object> e : td.entrySet()) {
                    DatabaseReference val = userdata.child(e.getKey()).child("live");
                    val.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Double lattitude = null;
                            Double longitude = null;
                            try {

                                 lattitude = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
                                 longitude = Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()));
                            }catch (Exception e){

                            }
                            if((lattitude != null) && (longitude != null)){
                               LatLng user = new LatLng(lattitude,longitude);
                                googleMap.addMarker(new MarkerOptions().position(user).title(e.getKey()));
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(30.24,60.44))
                .zoom(17)
                .bearing(0)
                .tilt(80)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
