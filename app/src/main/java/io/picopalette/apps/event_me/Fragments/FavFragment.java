package io.picopalette.apps.event_me.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.picopalette.apps.event_me.Activities.ListCreationActivity;
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fav, container, false);

        getActivity().setTitle("Favourites");

        mFavFAB = (FloatingActionButton) v.findViewById(R.id.favFAB);

        mFavFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage.matches("favourites")) {
                    Intent intent = new Intent(getActivity(), PeopleSearchActivity.class);
                    intent.putExtra("jobFor", "People Fragment");
                    intent.putExtra("job", "new");
                    intent.putExtra("title", "Add Favourites");
                    startActivity(intent);
                } else {
                    AlertDialog.Builder mB = new AlertDialog.Builder(getContext());
                    View alertView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_title,null);
                    final EditText listTitle = (EditText) alertView.findViewById(R.id.listTitleEditText);
                    TextView textView = (TextView) alertView.findViewById(R.id.listTitleTextView);
                    textView.setText("New Team");
                    listTitle.setHint("Enter a Name for the awesome team");
                    Button nextButton = (Button) alertView.findViewById(R.id.nextButton);
                    mB.setView(alertView);
                    final AlertDialog dialog = mB.create();
                    dialog.show();

                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(listTitle.getText().toString().equals(""))
                            {
                                Toast.makeText(getContext(),"Please enter the Team Name",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String title = listTitle.getText().toString();
                                Intent intent = new Intent(getContext(), PeopleSearchActivity.class);
                                intent.putExtra("teamName",title);
                                intent.putExtra("title", title);
                                intent.putExtra("jobFor", "Teams Fragment");
                                intent.putExtra("job", "edit");
                                startActivity(intent);
                                dialog.dismiss();
                            }

                        }
                    });
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
