package io.picopalette.apps.event_me.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.picopalette.apps.event_me.Fragments.EventsFragment;
import io.picopalette.apps.event_me.Fragments.ListsFragment;
import io.picopalette.apps.event_me.Fragments.ProfileFragment;
import io.picopalette.apps.event_me.Fragments.FavFragment;
import io.picopalette.apps.event_me.R;

public class MainActivity extends AppCompatActivity {
    private boolean isInEvent = true;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent2 = getIntent();
//        Intent intent2 = getIntent();
//        if(intent2.hasExtra("from")){
//            Log.d("frommm","frommm");
//            String inten = String.valueOf(intent2.getExtras().get("from"));
//            if(inten.matches("notif")){
//                Log.d("frommm22","frommm");
//                getSupportActionBar().hide();
//                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//                Bundle userBundle = new Bundle();
//                userBundle.putString("uid", fUser.getUid());
//                userBundle.putString("name", fUser.getDisplayName());
//                userBundle.putString("email", fUser.getEmail());
//                userBundle.putString("dpurl", fUser.getPhotoUrl().toString());
//                Fragment selectedFragment = ProfileFragment.newInstance();
//                selectedFragment.setArguments(userBundle);
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.content, selectedFragment);
//                transaction.commit();
//
//            }
//        }
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
            finish();
        }
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        getSupportActionBar().show();
                        switch (item.getItemId()) {
                            case R.id.navigation_events:
                                selectedFragment = EventsFragment.newInstance();
                                isInEvent = true;
                                break;
                            case R.id.navigation_lists:
                                selectedFragment = ListsFragment.newInstance();
                                isInEvent = false;
                                break;
                            case R.id.navigation_favourites:
                                selectedFragment = FavFragment.newInstance();
                                isInEvent = false;
                                break;
                            case R.id.navigation_profile:
                                getSupportActionBar().hide();
                                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                Bundle userBundle = new Bundle();
                                userBundle.putString("uid", fUser.getUid());
                                userBundle.putString("name", fUser.getDisplayName());
                                userBundle.putString("email", fUser.getEmail());
                                userBundle.putString("dpurl", fUser.getPhotoUrl().toString());
                                selectedFragment = ProfileFragment.newInstance();
                                selectedFragment.setArguments(userBundle);
                                isInEvent = false;
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, selectedFragment);
                        transaction.commit();
                        return true;
                    }

                });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, EventsFragment.newInstance());
        transaction.commit();

        if(intent2.hasExtra("from")){
            String val = String.valueOf(intent2.getExtras().get("from"));
            if(val.matches("notif")){
                getSupportActionBar().hide();
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                Bundle userBundle = new Bundle();
                userBundle.putString("uid", fUser.getUid());
                userBundle.putString("name", fUser.getDisplayName());
                userBundle.putString("email", fUser.getEmail());
                userBundle.putString("dpurl", fUser.getPhotoUrl().toString());
                Fragment selectedFragment = ProfileFragment.newInstance();
                selectedFragment.setArguments(userBundle);

                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, selectedFragment);
                transaction.commit();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.navigation_notification) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        }
        return true;
    }



    @Override
    public void onBackPressed() {
        if(isInEvent)
            super.onBackPressed();
        else {
            getSupportActionBar().show();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, EventsFragment.newInstance());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_events);
            isInEvent = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}