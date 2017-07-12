package io.picopalette.apps.event_me.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.picopalette.apps.event_me.Activities.PeopleSearchActivity;
import io.picopalette.apps.event_me.R;

public class FavFragment extends Fragment {

    private FloatingActionButton mFavFAB;
    private String currentPage = "favourites";

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

        getActivity().setTitle("Favourites");

        mFavFAB = (FloatingActionButton) v.findViewById(R.id.favFAB);

        mFavFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage.matches("favourites")) {
                    Intent intent = new Intent(getActivity(), PeopleSearchActivity.class);
                    startActivity(intent);
                }
            }
        });

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

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position)
                {
                    case 0:
                        mFavFAB.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_person_add_white_24dp));
                        currentPage = "favourites";
                        break;

                    case 1:
                        mFavFAB.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_group_add_white_24dp));
                        currentPage = "teams";
                        break;
                    default:
                        mFavFAB.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_person_add_white_24dp));
                        currentPage = "favourites";
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;


    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().setTitle(getResources().getString(R.string.app_name));

    }
}
