package io.picopalette.apps.event_me.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import io.picopalette.apps.event_me.Activities.MainActivity;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class LocationData extends Service {

    private FirebaseUser user;
    private DatabaseReference userData;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private String useremail;
    private String photourl;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Event-Me");
        mBuilder.setContentText("you are in live");
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("from","notif");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(89, mBuilder.build());


        userData = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference users = userData.child("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        useremail = Utilities.encodeEmail(user.getEmail());
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(useremail).child("dpUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                photourl = String.valueOf(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                users.child(useremail).child("live").child("lat").setValue(location.getLatitude());
                users.child(useremail).child("live").child("lon").setValue(location.getLongitude());
                users.child(useremail).child("live").child("timestamp").setValue(Calendar.getInstance().getTime().getTime() + "");
                users.child(useremail).child("live").child("url").setValue(photourl);
//                Intent i = new Intent("location_data");
//                i.putExtra("lattitude",location.getLatitude());
//                i.putExtra("longitude",location.getLongitude());
//                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setSmallIcon(R.drawable.logo);
                mBuilder.setContentTitle("Event-Me");
                mBuilder.setContentText("you are in live");
                Intent i = new Intent(getBaseContext(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("from","notif");
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setOngoing(true);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(89, mBuilder.build());




            }

            @Override
            public void onProviderDisabled(String provider) {


                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(89);
                    sendNotification("You have disabled your network and location services enable it again to share it with your friends");
                Log.d("providerdis",provider);
                stopSelf();

            }
        };

        locationManager  = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,0,locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,0,locationListener);
    }

    private void sendNotification(String s) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Event-Me");
        mBuilder.setContentText(s);

        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        i.putExtra("from","notif");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(001, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationListener != null){
            locationManager.removeUpdates(locationListener);
        }
    }
}
