package io.picopalette.apps.event_me.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.picopalette.apps.event_me.Fragments.EventAddParFragment;
import io.picopalette.apps.event_me.Fragments.EventAddTeamFragment;
import io.picopalette.apps.event_me.Fragments.EventParFragment;
import io.picopalette.apps.event_me.R;

public class AddEventParticipantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_participants);

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
            public CharSequence getPageTitle(int position) {

                switch (position)
                {
                    case 0:
                        return "PAR";
                    case 1:
                        return "ADD PAR";
                    default:
                        return "Add Teams";
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLays);
        tabLayout.setupWithViewPager(vp);
    }
}
