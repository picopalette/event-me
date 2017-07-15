package io.picopalette.apps.event_me.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import io.picopalette.apps.event_me.Models.SimpleContact;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Services.LocationData;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class LiveShare extends FragmentActivity  implements OnMapReadyCallback{

    private Double lat, lon;
    private DatabaseReference userdata, livedata;
    private FirebaseUser user;
    private String key;
    private Bitmap image;
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveshare);
        builder = new LatLngBounds.Builder();
        userdata = FirebaseDatabase.getInstance().getReference().child(Constants.users);

        user = FirebaseAuth.getInstance().getCurrentUser();
        key = (String) getIntent().getExtras().get("uid");
        livedata = FirebaseDatabase.getInstance().getReference(Constants.events).child(key).child(Constants.livepart);
        lat = (Double) getIntent().getExtras().get("lat");
        lon = (Double) getIntent().getExtras().get("lon");
        builder.include(new LatLng(lat,lon));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//            Intent  i = new Intent(getApplicationContext(), LocationData.class);
//            startService(i);


    }

//    private boolean permission_check() {
//        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission
//                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission
//                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 69);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 69){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(LiveShare.this,"Permission Granted", Toast.LENGTH_LONG).show();
//            }
//            else{
//                permission_check();
//            }
//        }
//    }






    @Override
    public void onMapReady(final GoogleMap googleMap) {

        livedata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("inside onmap", String.valueOf(dataSnapshot.getValue()));
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                for (final Map.Entry<String, Object> e : td.entrySet()) {
                    final Marker[] marker = new Marker[1];
                    Log.d("mapview32",e.getKey()+ " "+dataSnapshot.child(e.getKey()).getValue());

                    if(Objects.equals(dataSnapshot.child(e.getKey()).getValue().toString(), "true"))
                    {

                         FirebaseDatabase.getInstance().getReference().child(Constants.users).child(e.getKey()).child("live").addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 Log.d("mapview67", String.valueOf(dataSnapshot.getValue()));

                                 Double lattitude = null;
                                 Double longitude = null;
                                 String timestamp = null;
                                 String url2 = null;
                                 try {
                                     if(Objects.equals(e.getKey().toString(), Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())))
                                     {
                                         lattitude = null;
                                         longitude = null;
                                     }
                                     else {
                                         url2 = String.valueOf(dataSnapshot.child("url").getValue());
                                         lattitude = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
                                         longitude = Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()));
                                         String time = String.valueOf(dataSnapshot.child("timestamp").getValue());
                                     }
                                     Log.d("gettings",lattitude+" "+longitude );


                                 } catch (Exception e) {

                                 }
                                 if ((lattitude != null) && (longitude != null)) {
                                     Log.d("gettings2",lattitude+" "+longitude );
                                     LatLng user = new LatLng(Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue())),Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()))) ;
                                     if(marker[0] != null){
                                         marker[0].remove();
                                     }
                                     try {

                                         Bitmap bmImg = Ion.with(LiveShare.this)
                                                 .load(url2).asBitmap().get();
//                                         final Rect rect = new Rect(0, 0, bmImg.getWidth(), bmImg.getHeight());
//                                         final RectF rectF = new RectF(rect);
//                                         Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//                                         Bitmap bmp = Bitmap.createBitmap(50, 50, conf);
//                                         Canvas canvas1 = new Canvas(bmp);
//                                         Paint color = new Paint();
//                                         color.setTextSize(35);
////                                         canvas1.drawBitmap(bmImg, 0,0, color);
//                                         canvas1.drawOval(rectF,color);

                                         final Bitmap output = Bitmap.createBitmap(bmImg.getWidth(),
                                                 bmImg.getHeight(), Bitmap.Config.ARGB_8888);
                                         final Canvas canvas = new Canvas(output);

                                         final int color = Color.RED;
                                         final Paint paint = new Paint();
                                         final Rect rect = new Rect(0, 0, bmImg.getWidth(), bmImg.getHeight());
                                         final RectF rectF = new RectF(rect);

                                         paint.setAntiAlias(true);
                                         canvas.drawARGB(0, 0, 0, 0);
                                         paint.setColor(color);
                                         canvas.drawOval(rectF, paint);

                                         paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                                         canvas.drawBitmap(bmImg, rect, rect, paint);

                                         bmImg.recycle();

                                         marker[0] = googleMap.addMarker(new MarkerOptions().position(user)
                                                 .title(e.getKey())
                                                 .icon(BitmapDescriptorFactory.fromBitmap(output))
                                                 .anchor(0.5f, 1)
                                                 .snippet((String) DateUtils.getRelativeTimeSpanString(Long.parseLong(String.valueOf(dataSnapshot.child("timestamp").getValue())), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)));

                                         builder.include(user);
                                          bounds = builder.build();

                                     } catch (InterruptedException e1) {
                                         e1.printStackTrace();
                                     } catch (ExecutionException e1) {
                                         e1.printStackTrace();
                                     }

                                     googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

                                 }


                             }
                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });
//                        val.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Log.d("mapview67", String.valueOf(dataSnapshot.getValue()));
//
//                                Double lattitude = null;
//                                Double longitude = null;
//                                try {
//                                     lattitude = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
//                                     longitude = Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()));
//                                    Log.d("gettings",lattitude+" "+longitude );
//
//
//                                } catch (Exception e) {

//                                }
//                                if ((lattitude != null) && (longitude != null)) {
//                                    Log.d("gettings2",lattitude+" "+longitude );
//                                    LatLng user = new LatLng(Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue())),Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()))) ;
//                                    googleMap.addMarker(new MarkerOptions().position(user).title(e.getKey()).snippet(String.valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(String.valueOf(dataSnapshot.child("timestamp").getValue())),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS))));
//                                }
//
//
//                            }
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






//        livedata.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
//                for (final Map.Entry<String, Object> e : td.entrySet()) {
//                    DatabaseReference val = userdata.child(e.getKey()).child("live");
//                    val.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            Double lattitude = null;
//                            Double longitude = null;
//                            try {
//
//                                lattitude = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
//                                longitude = Double.valueOf(String.valueOf(dataSnapshot.child("lon").getValue()));
//                            }catch (Exception e){
//
//                            }
//                            if((lattitude != null) && (longitude != null)){
//                                LatLng user = new LatLng(lattitude,longitude);
//                                googleMap.addMarker(new MarkerOptions().position(user).title(e.getKey()));
//                            }
//
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });






    }
}