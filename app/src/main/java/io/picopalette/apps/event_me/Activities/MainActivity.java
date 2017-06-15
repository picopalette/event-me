package io.picopalette.apps.event_me.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import io.picopalette.apps.event_me.Fragments.EventsFragment;
import io.picopalette.apps.event_me.Fragments.NotificationFragment;
import io.picopalette.apps.event_me.Fragments.ProfileFragment;
import io.picopalette.apps.event_me.Fragments.TeamsFragment;
import io.picopalette.apps.event_me.R;

public class MainActivity extends AppCompatActivity {

    int mFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
            finish();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
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
                                break;
                            case R.id.navigation_teams:
                                selectedFragment = TeamsFragment.newInstance();
                                break;
                            case R.id.navigation_profile:
                                getSupportActionBar().hide();
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                            case R.id.navigation_home:
                                selectedFragment = NotificationFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, selectedFragment);
                        if(mFlag==0)
                        {
                            transaction.addToBackStack(null);
                            mFlag=1;
                        }
                        transaction.commit();
                        return true;
                    }
                });
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, EventsFragment.newInstance());
        transaction.commit();
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, EventsFragment.newInstance());
        transaction.commit();
        mFlag=0;
    }
}