package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 15-07-2017.
 */

public class EventAddParFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder> recyclerAdapter;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.people_recView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final String eventId = getActivity().getIntent().getStringExtra("eventId");
        DatabaseReference favRef = dbRef.child(Constants.users).child(Utilities.encodeEmail(currentUser.getEmail())).child(Constants.favContacts);
        final DatabaseReference eventParRef = dbRef.child(Constants.events).child(eventId).child("participants");
        final DatabaseReference eventLiveParRef = dbRef.child(Constants.events).child(eventId).child("liveparticipants");
        final DatabaseReference usersRef = dbRef.child(Constants.users);

        recyclerAdapter = new FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder>(String.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, favRef) {
            @Override
            protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, final String model, int position) {
                viewHolder.emailView.setText(model);
                usersRef.child(Utilities.encodeEmail(model)).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.nameView.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                usersRef.child(Utilities.encodeEmail(model)).child("dpUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Glide.with(getActivity().getApplicationContext())
                                .load(dataSnapshot.getValue(String.class))
                                .into(viewHolder.imageView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.addButton.setVisibility(View.VISIBLE);

                eventParRef.child(Utilities.encodeEmail(model)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(isAdded()) {
                            if (dataSnapshot.exists()) {
                                viewHolder.addButton.setOnClickListener(null);
                                viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                            } else {
                                viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_blue_24dp));
                                viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        eventParRef.child(Utilities.encodeEmail(model)).setValue(Constants.UserStatus.INVITED);
                                        eventLiveParRef.child(Utilities.encodeEmail(model)).setValue(false);
                                        usersRef.child(Utilities.encodeEmail(model)).child(Constants.events).child(eventId).setValue(Constants.UserStatus.INVITED);
                                        viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        recyclerView.setAdapter(recyclerAdapter);

    }
}
