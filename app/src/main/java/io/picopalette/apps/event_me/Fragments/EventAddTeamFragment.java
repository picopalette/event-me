package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Activities.PeopleSearchActivity;
import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Adapters.PersonalListsViewHolder;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 15-07-2017.
 */

public class EventAddTeamFragment extends Fragment {

    private RecyclerView teamsRecView;
    private FirebaseRecyclerAdapter<Object, PersonalListsViewHolder> recyclerAdapter;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef = dbRef.child(Constants.users);
    private DatabaseReference favRef = userRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favTeams);
    private PersonalListsViewHolder lastViewHolder;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        teamsRecView = (RecyclerView) view.findViewById(R.id.people_recView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String eventId = getActivity().getIntent().getStringExtra("eventId");
        final DatabaseReference eventParRef = dbRef.child(Constants.events).child(eventId).child("participants");
        final DatabaseReference eventLiveParRef = dbRef.child(Constants.events).child(eventId).child("liveparticipants");
        recyclerAdapter = new FirebaseRecyclerAdapter<Object, PersonalListsViewHolder>(Object.class, R.layout.card_list, PersonalListsViewHolder.class, favRef) {
            @Override
            protected void populateViewHolder(final PersonalListsViewHolder viewHolder, Object model, final int position) {
                viewHolder.personalListTitle.setText(getRef(viewHolder.getAdapterPosition()).getKey());
                viewHolder.personalListCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.personalListActions.getVisibility() == View.VISIBLE)
                            viewHolder.personalListActions.setVisibility(View.GONE);
                        else {
                            clicked(viewHolder);
                            viewHolder.personalListActions.setVisibility(View.VISIBLE);
                        }
                    }
                });
                viewHolder.personalListView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PeopleSearchActivity.class);
                        intent.putExtra("teamName",getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("title", getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("jobFor", "Teams Fragment");
                        intent.putExtra("job", "view");
                        startActivity(intent);
                    }
                });
                viewHolder.personalListEdit.setVisibility(View.GONE);
                viewHolder.personalListDeleteIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_add_blue_24dp));
                viewHolder.personalListDeleteText.setText("Add All");
                viewHolder.personalListDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                        builder = new AlertDialog.Builder(getActivity());
//                        }
                        builder.setTitle("Add Entire Team")
                                .setMessage("All the members in " + getRef(viewHolder.getAdapterPosition()).getKey() + " will be added to the Event")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with add
                                        getRef(viewHolder.getAdapterPosition()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot participant : dataSnapshot.getChildren()) {
                                                    String parEmail = participant.getValue(String.class);
                                                    eventParRef.child(Utilities.encodeEmail(parEmail)).setValue(Constants.UserStatus.INVITED);
                                                    eventLiveParRef.child(Utilities.encodeEmail(parEmail)).setValue(false);
                                                    userRef.child(Utilities.encodeEmail(parEmail)).child(Constants.events).child(eventId).setValue(Constants.UserStatus.INVITED);
                                                    Toast.makeText(getActivity(), "All are added to Event", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(R.drawable.ic_group_add_white_24dp)
                                .show();

                    }
                });
            }
        };
        teamsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        teamsRecView.setAdapter(recyclerAdapter);
    }

    private void clicked(PersonalListsViewHolder viewHolder) {
        if(lastViewHolder != null) {
            lastViewHolder.personalListActions.setVisibility(View.GONE);
        }
        lastViewHolder = viewHolder;
    }
}
