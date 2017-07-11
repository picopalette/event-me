package io.picopalette.apps.event_me.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.picopalette.apps.event_me.Utils.Utilities;

public class LocationData extends Service {

    private FirebaseUser user;
    private DatabaseReference userData;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private String useremail;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        userData = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference users = userData.child("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        useremail = Utilities.encodeEmail(user.getEmail());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                users.child(useremail).child("live").child("lat").setValue(location.getLatitude());
                users.child(useremail).child("live").child("lon").setValue(location.getLongitude());
                Intent i = new Intent("location_data");
                i.putExtra("lattitude",location.getLatitude());
                i.putExtra("longitude",location.getLongitude());
                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {



            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };

        locationManager  = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,0,locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
