package io.picopalette.apps.event_me.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.picopalette.apps.event_me.R;



public class ListsFragment extends Fragment {
    public static ListsFragment newInstance() {
        return new ListsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);

        final EventListsFragment eventListsFragment = new EventListsFragment();
        final PersonalListsFragment personalListsFragment = new PersonalListsFragment();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.listsViewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position==0 ? eventListsFragment : personalListsFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position==0 ? "Events'" : "Personal";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabL);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
