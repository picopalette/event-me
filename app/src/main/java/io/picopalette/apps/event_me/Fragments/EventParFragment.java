package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class EventParFragment extends Fragment {

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

        DatabaseReference partRef = dbRef.child(Constants.events).child(getActivity().getIntent().getStringExtra("eventId")).child("participants");
        final DatabaseReference usersRef = dbRef.child(Constants.users);

        recyclerAdapter = new FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder>(String.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, partRef) {
            @Override
            protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, String model, int position) {
                String email = getRef(viewHolder.getAdapterPosition()).getKey();
                viewHolder.emailView.setText(Utilities.decodeEmail(email));
                usersRef.child(email).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.nameView.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                usersRef.child(email).child("dpUrl").addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.d("email", Utilities.decodeEmail(email));
                Log.d("cu", currentUser.getEmail());
                if(String.valueOf(Utilities.decodeEmail(email)).matches(String.valueOf(currentUser.getEmail()))) {
                    viewHolder.addButton.setVisibility(View.INVISIBLE);
                    Log.d("you", "owner");

                } else {
                    viewHolder.addButton.setVisibility(View.VISIBLE);
                    viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_blue_24dp));
                    viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                            builder = new AlertDialog.Builder(getActivity());
//                        }
                            builder.setTitle("Delete Favourite")
                                    .setMessage(viewHolder.nameView.getText() + " will be removed from Event")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            getRef(viewHolder.getAdapterPosition()).removeValue();

                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    });
                }

            }
        };

        recyclerView.setAdapter(recyclerAdapter);

    }
}
