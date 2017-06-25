package io.picopalette.apps.event_me.Fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.picopalette.apps.event_me.R;

public class FavFragment extends Fragment {

    public static FavFragment newInstance() {
        return new FavFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fav, container, false);

        final PeopleFragment peopleFragment = new PeopleFragment();
        final TeamsFragment teamsFragment = new TeamsFragment();

        ViewPager vp = (ViewPager) v.findViewById(R.id.favViewPager);

        vp.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position==0 ? peopleFragment : teamsFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position==0 ? "People" : "Teams";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabLay);
        tabLayout.setupWithViewPager(vp);
        return v;
    }
}
