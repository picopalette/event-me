package io.picopalette.apps.event_me.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mzelzoghbi.zgallery.ZGallery;
import com.mzelzoghbi.zgallery.entities.ZColor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.picopalette.apps.event_me.Activities.EventSearchActivity;
import io.picopalette.apps.event_me.Activities.MainActivity;
import io.picopalette.apps.event_me.Adapters.EventsAdapter;
import io.picopalette.apps.event_me.Activities.EventCreationActivity;
import io.picopalette.apps.event_me.EventMeApp;
import io.picopalette.apps.event_me.Interfaces.RecyclerViewReadyCallback;
import io.picopalette.apps.event_me.Models.DateAndTime;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.EventGallery;
import io.picopalette.apps.event_me.Utils.Utilities;


public class EventsFragment extends Fragment  {

    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private EventsAdapter adapter;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences eventkeys;
    private CardView mSearchCard;
    private boolean activityStartUp = true;
    private Date currentDate;
    private Calendar calendar;
    private Context context = getContext();
    private View emptyView;


    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events, container,false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_eve);
        emptyView = (View) v.findViewById(R.id.fragment_events_emptyView);
        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        Log.d("current", currentDate.toString());

        getDataTask();
        events = new ArrayList<>();
        mSearchCard = (CardView) v.findViewById(R.id.publicEventsSearchCard);
        recyclerView.setHasFixedSize(true);
        adapter = new EventsAdapter(getActivity().getApplicationContext(), events,recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((TextView)emptyView.findViewById(R.id.empty_textView)).setText("No Events Found, \nTry creating one or joining one");
        ((ImageView)emptyView.findViewById(R.id.empty_imageView)).setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_emoticon_black_24dp));

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }
        });
        adapter.notifyDataSetChanged();

//
//        mPES.setQueryHint("Search Public Events");
        mSearchCard.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Intent intnt = new Intent( getActivity(),EventSearchActivity.class );
                intnt.putExtra( "mylist", (Serializable) adapter.events );
                startActivity(  intnt);

            }
        } );

//        mPES.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventGallery.with(EventsFragment.this.getActivity(), "eventId")
//                        .setToolbarTitleColor(ZColor.WHITE) // toolbar title color
//                        .setGalleryBackgroundColor(ZColor.WHITE) // activity background color
//                        .setToolbarColorResId(R.color.colorPrimary) // toolbar color
//                        .setTitle("eventId") // toolbar title
//                        .show();
//            }
//        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventCreationActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return v;
    }

    private void getDataTask()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference eventReference = mDatabaseReference.child(Constants.users).child(Utilities.encodeEmail(user.getEmail())).child(Constants.events);
                eventReference.keepSynced(true);
                eventReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("TESTI","Inddfs datasnaphot of eventfragment");
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
                                        if(event != null) {
                                            Log.d("TESTI67", String.valueOf(event));
                                            DateAndTime dateAndTime = event.getDateAndTime();
                                            Date eventDate = new Date(dateAndTime.getYear() - 1900, dateAndTime.getMonth(), dateAndTime.getDayOfMonth(), dateAndTime.getHourOfDay(), dateAndTime.getMinute());
                                            Date eventEndDate = new Date(dateAndTime.getYear() - 1900, dateAndTime.getMonth(), dateAndTime.getDayOfMonth(), dateAndTime.getEndHourOfDay(), dateAndTime.getEndMinute());
                                            Log.d("currentdate", currentDate.toString());
                                            Log.d("eventdate", eventDate.toString());
                                            Log.d("eventEnd", eventEndDate.toString());
                                            if (eventEndDate.after(currentDate)) {
                                                if (eventDate.before(currentDate)) {
                                                    Log.d("ongoing", event.toString());
                                                    event.setStatus(Constants.EventStatus.ONGOING);
                                                }
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
                                        else
                                            adapter.events.clear();
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


}