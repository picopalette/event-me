package io.picopalette.apps.event_me.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

import io.picopalette.apps.event_me.Adapters.EventsAdapter;
import io.picopalette.apps.event_me.Activities.EventCreationActivity;
import io.picopalette.apps.event_me.Interfaces.RecyclerViewReadyCallback;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;


public class EventsFragment extends Fragment  {

    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private EventsAdapter adapter;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences eventkeys;
    private SearchView mPES;
    private boolean activityStartUp = true;


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
        getDataTask();
        events = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        adapter = new EventsAdapter(getActivity(), events,recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPES = (SearchView) v.findViewById(R.id.publicEventsSearchView);

        mPES.setQueryHint("Search Public Events");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventCreationActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return v;
    }

    private void getDataTask() {
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
                    DatabaseReference eventRef = mDatabaseReference.child(Constants.events).child(eventSnapshot.getKey());
                    Log.d("Testi", eventSnapshot.getValue().toString());
                    if (Objects.equals(eventSnapshot.getValue().toString(), Constants.UserStatus.OWNER.toString()) || Objects.equals(eventSnapshot.getValue().toString(), Constants.UserStatus.GOING.toString())) {

                        Log.d("testify", "indie the if statement");
                        eventRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                Log.d("TESTI", eventSnapshot.getValue().toString());
                                if (event != null && !adapter.events.contains(event)) {
                                    adapter.events.add(event);
                                } else if(event != null && adapter.events.contains(event)) {
                                    adapter.events.set(adapter.events.indexOf(event), event);
                                }
                                Log.d("testt", String.valueOf(events));
                                adapter.notifyDataSetChanged();
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