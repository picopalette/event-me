package io.picopalette.apps.event_me.Fragments;


import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.picopalette.apps.event_me.Activities.EventCreationActivity;
import io.picopalette.apps.event_me.Activities.EventDisplayActivity;
import io.picopalette.apps.event_me.Adapters.TimelineAdapter;
import io.picopalette.apps.event_me.Models.DateAndTime;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.Models.Location;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Services.LocationData;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Util;
import io.picopalette.apps.event_me.Utils.Utilities;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener{


    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private TimelineAdapter adapter;
    private DatabaseReference mDatabaseReference;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    private Bundle userBundle;
    private Uri coverUri = Uri.parse("");
    private Uri imageUri = Uri.parse("http://i.imgur.com/VIlcLfg.jpg");

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private TextView profileName;
    private TextView profileEmail;
    private Date currentDate;
    private Calendar calendar;
    private SimpleDraweeView avatar;
    private Switch myswitch;
    private NotificationManager mNotificationManager;
    private LocationManager locationManager;
    private TextView mMainLive;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(this.getContext());
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        userBundle = getArguments();
        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        appbar = (AppBarLayout) v.findViewById( R.id.appbar );
        collapsing = (CollapsingToolbarLayout) v.findViewById( R.id.collapsing );
        coverImage = (ImageView) v.findViewById( R.id.imageview_placeholder );
        framelayoutTitle = (FrameLayout) v.findViewById( R.id.framelayout_title );
        linearlayoutTitle = (LinearLayout) v.findViewById( R.id.linearlayout_title );
        toolbar = (Toolbar) v.findViewById( R.id.toolbar );
        textviewTitle = (TextView) v.findViewById( R.id.textview_title );
        profileName = (TextView) v.findViewById(R.id.profile_view_name);
        profileEmail = (TextView) v.findViewById(R.id.profile_view_email);
        mMainLive = (TextView) v.findViewById(R.id.mainLiveShare);
        myswitch = (Switch) v.findViewById(R.id.mainSwitch);
        avatar = (SimpleDraweeView) v.findViewById(R.id.avatar);
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
        //set avatar and cover
        imageUri = Uri.parse(userBundle.getString("dpurl"));
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(userBundle.getString("email"))).child("dpUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                imageUri = Uri.parse(url);
                avatar.setImageURI(imageUri);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if(isServiceRunning(LocationData.class)){
            myswitch.setChecked(true);
        }
        else
        {
            myswitch.setChecked(false);
        }

        myswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceRunning(LocationData.class)){
                    mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(89);
                    Intent i = new Intent(getContext(), LocationData.class);
                    getContext().stopService(i);
                    myswitch.setChecked(false);

                }else
                {
                    if(!permissionEnabled()) {
                        Intent i = new Intent(getContext(), LocationData.class);
                        getContext().startService(i);
                    }
                    if(isServiceRunning(LocationData.class)){
                        myswitch.setChecked(true);
                    }
                    else
                    {
                        myswitch.setChecked(false);
                    }
                }
            }
        });
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mMainLive.setTextColor(Color.parseColor("#094782"));
                    mMainLive.setTypeface(null, Typeface.BOLD);
                }
                else
                {
                    mMainLive.setTextColor(Color.parseColor("#666666"));
                    mMainLive.setTypeface(null, Typeface.NORMAL);
                }
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDp();
            }
        });
        coverImage.setImageDrawable(getResources().getDrawable(R.drawable.full));
        profileName.setText(userBundle.getString("name"));
        profileEmail.setText(userBundle.getString("email"));
        recyclerView = (RecyclerView) v.findViewById(R.id.timelineRecycle);
        getDataTask();
        events = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        adapter = new TimelineAdapter(getActivity().getApplicationContext(), events,recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

    private boolean permissionEnabled() {

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),Manifest.permission
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
                Toast.makeText(getContext(),"Permission Granted", Toast.LENGTH_LONG).show();
//                Intent i = new Intent(getContext(), LocationData.class);
//                getContext().startService(i);
//                myswitch.setChecked(true);

            }
            else{
                permissionEnabled();
            }
        }
    }

    private boolean isServiceRunning(Class<LocationData> locationDataClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (locationDataClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void getDataTask(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventReference = mDatabaseReference.child(Constants.users).child(Utilities.encodeEmail(user.getEmail())).child(Constants.events);
        eventReference.keepSynced(true);
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TESTI","Inddfs datasnaphot of Profilefragment");
                for(final DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Log.d("countsss", String.valueOf(dataSnapshot.getChildrenCount()));
                    Log.d("counts", String.valueOf(dataSnapshot.getChildrenCount()));
                    final DatabaseReference eventRef = mDatabaseReference.child(Constants.events).child(eventSnapshot.getKey());
                    Log.d("Testi", eventSnapshot.getValue().toString());
                    if (Objects.equals(eventSnapshot.getValue().toString(), Constants.UserStatus.OWNER.toString()) || Objects.equals(eventSnapshot.getValue().toString(), Constants.UserStatus.GOING.toString())) {

                        Log.d("testify", "indie the if statement");
                        eventRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                Log.d("TESTI", eventSnapshot.getValue().toString());
                                DateAndTime dateAndTime = event.getDateAndTime();
                                Date eventDate = new Date(dateAndTime.getYear() - 1900, dateAndTime.getMonth(), dateAndTime.getDayOfMonth(), dateAndTime.getHourOfDay(), dateAndTime.getMinute());
                                Date eventEndDate = new Date(dateAndTime.getYear() - 1900, dateAndTime.getMonth(), dateAndTime.getDayOfMonth(), dateAndTime.getEndHourOfDay(), dateAndTime.getEndMinute());

                                if(eventEndDate.before(currentDate)) {
                                    if (event != null && !adapter.events.contains(event)) {
                                        adapter.events.add(event);
                                    } else if (event != null && adapter.events.contains(event)) {
                                        adapter.events.set(adapter.events.indexOf(event), event);
                                    }
                                    Log.d("testt", String.valueOf(events));
                                    adapter.notifyDataSetChanged();
                                } else {
                                    eventRef.removeEventListener(this);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        });

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),getString(R.string.error_network),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void changeDp() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity());
    }

    private void saveDpUrl(String url) {
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("dpUrl").setValue(url);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d("ProfileFragment", "OnActivityResult");
                final Uri resultUri = result.getUri();
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("Your new Display Picture is cool!");
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("dp/" + Utilities.encodeEmail(userBundle.getString("email"))).putFile(resultUri);
                progressDialog.show();
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(),"Cannot Upload",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        saveDpUrl(downloadUrl);
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0 * (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress((int) progress);
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
                        avatar.setImageURI(resultUri);
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}