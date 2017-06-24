package io.picopalette.apps.event_me.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.picopalette.apps.event_me.Adapters.NotificationAdapter;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private NotificationAdapter adapter;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        recyclerView = (RecyclerView) findViewById(R.id.notification_rec_view);
        events = new ArrayList<>();
        adapter = new NotificationAdapter(getApplicationContext(), events);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void getDataTask() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventReference = mDatabaseReference.child(Constants.users).child(Utilities.encodeEmail(user.getEmail())).child(Constants.events);
        eventReference.keepSynced(true);
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference eventRef = mDatabaseReference.child(Constants.events).child(eventSnapshot.getKey());
                    if(eventSnapshot.getValue() == Constants.UserStatus.INVITED) {
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                adapter.events.add(event);
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
                Toast.makeText(getApplicationContext(),getString(R.string.error_network),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.events.clear();
        getDataTask();
    }

}
