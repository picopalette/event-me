package io.picopalette.apps.event_me.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import io.picopalette.apps.event_me.Fragments.EventAddParFragment;
import io.picopalette.apps.event_me.Fragments.EventAddTeamFragment;
import io.picopalette.apps.event_me.Fragments.EventParFragment;
import io.picopalette.apps.event_me.R;

public class AddEventParticipantsActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_participants);

        eventId = getIntent().getStringExtra("eventId");
        if(getIntent().hasExtra("eventName"))
            getSupportActionBar().setTitle(getIntent().getStringExtra("eventName"));
        else
            getSupportActionBar().setTitle("Add Participants");

        final EventAddParFragment eventAddParFragment = new EventAddParFragment();
        final EventParFragment eventParFragment = new EventParFragment();
        final EventAddTeamFragment eventAddTeamFragment = new EventAddTeamFragment();

        ViewPager vp = (ViewPager) findViewById(R.id.addParViewPager);
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position)
                {
                    case 0:
                        return eventParFragment;
                    case 1:
                        return eventAddParFragment;
                    default:
                        return eventAddTeamFragment;
                }
            }



            @Override
            public int getCount() {
                return 3;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLays);
        tabLayout.setupWithViewPager(vp);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_add_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_group_add_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parti_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.parti_done_menu:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
