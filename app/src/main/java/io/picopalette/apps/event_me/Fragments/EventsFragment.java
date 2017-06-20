package io.picopalette.apps.event_me.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import io.picopalette.apps.event_me.Adapters.EventsAdapter;
import io.picopalette.apps.event_me.Activities.EventCreationActivity;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;


public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private EventsAdapter adapter;
    private DatabaseReference mDatabaseReference;

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
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_eve);
        recyclerView.setHasFixedSize(true);
        events = new ArrayList<>();
        adapter = new EventsAdapter(getContext(), events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"fab clicked",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), EventCreationActivity.class);
                getActivity().startActivity(intent);
            }
        });
        getDataTask();
        return v;
    }

    private void getDataTask() {
        Log.d("tes1","inside getdatatask");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventReference = mDatabaseReference.child(Constants.users).child(Utilities.encodeEmail(user.getEmail())).child(Constants.events);
        Log.d("tes3","got event reference"+ eventReference.toString());
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("tes1","got datasnapshot"+ dataSnapshot.toString());
                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Log.d("testing",dataSnapshot.getChildren().toString());
                    DatabaseReference eventRef = mDatabaseReference.child(Constants.events).child(eventSnapshot.getKey());
                    Log.d("keys",eventSnapshot.getKey());
                    eventRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Event event = dataSnapshot.getValue(Event.class);
                            Log.d("tes2","got datasnapshot"+ event.toString());
                            adapter.events.add(event);
                            adapter.notifyDataSetChanged();
                            Log.d("tes2", adapter.events.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("tes3","cancelled");

                        }

                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),getString(R.string.error_network),Toast.LENGTH_LONG).show();
            }
        });


        eventReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),getString(R.string.error_network),Toast.LENGTH_LONG).show();
            }
        });
    }
}