package io.picopalette.apps.event_me.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.picopalette.apps.event_me.Activities.ListCreationActivity;
import io.picopalette.apps.event_me.R;



public class ListsFragment extends Fragment {

    private FloatingActionButton mListFAB;
    public static final String mListTitle = "List Title";

    public static ListsFragment newInstance() {
        return new ListsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);

        getActivity().setTitle("Lists");

        mListFAB = (FloatingActionButton) view.findViewById(R.id.listsFAB);

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

        mListFAB.hide();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        mListFAB.hide();
                        break;
                    case 1:
                        mListFAB.show();
                        break;
                    default:
                        mListFAB.hide();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mB = new AlertDialog.Builder(getContext());
                View alertView = inflater.inflate(R.layout.dialog_list_title,null);
                final EditText listTitle = (EditText) alertView.findViewById(R.id.listTitleEditText);
                Button nextButton = (Button) alertView.findViewById(R.id.nextButton);
                mB.setView(alertView);
                final AlertDialog dialog = mB.create();
                dialog.show();

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(listTitle.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(),"Please enter the List's Title",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String title = listTitle.getText().toString();
                            Intent intent = new Intent(getContext(), ListCreationActivity.class);
                            intent.putExtra(mListTitle,title);
                            intent.putExtra("type", "personal");
                            startActivity(intent);
                            dialog.dismiss();
                        }

                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().setTitle(getResources().getString(R.string.app_name));

    }
}
