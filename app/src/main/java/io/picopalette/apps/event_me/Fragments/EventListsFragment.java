package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Activities.EventDisplayActivity;
import io.picopalette.apps.event_me.Activities.ListCreationActivity;
import io.picopalette.apps.event_me.Activities.ListDisplayActivity;
import io.picopalette.apps.event_me.Adapters.PersonalListsViewHolder;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 25-06-2017.
 */

public class EventListsFragment extends Fragment {

    public static final String mListTitle = "List Title";
    private RecyclerView eventListsRecyclerViewOwner;
    private RecyclerView eventListsRecyclerViewMember;
    private FirebaseRecyclerAdapter<Object, PersonalListsViewHolder> ownerlistsAdapter;
    private FirebaseRecyclerAdapter<Object, PersonalListsViewHolder> memberlistsAdapter;
    private DatabaseReference mListReference;
    private Query ownerList;
    private Query memberList;
    private PersonalListsViewHolder lastViewHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_lists, container, false);

        mListReference = FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.events);
        ownerList = mListReference.orderByValue().equalTo(Constants.UserStatus.OWNER.toString());
        memberList = mListReference.orderByValue().equalTo(Constants.UserStatus.GOING.toString());
        eventListsRecyclerViewOwner = (RecyclerView) v.findViewById(R.id.event_lists_RecyclerView_owner);
        eventListsRecyclerViewMember = (RecyclerView) v.findViewById(R.id.event_lists_RecyclerView_member);

        ownerlistsAdapter = new FirebaseRecyclerAdapter<Object, PersonalListsViewHolder>(Object.class, R.layout.card_list, PersonalListsViewHolder.class, ownerList) {
            @Override
            protected void populateViewHolder(final PersonalListsViewHolder viewHolder, Object model, final int position) {
                Log.d("Event_List_ids", getRef(position).getKey());
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(Constants.events).child(getRef(position).getKey());
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.personalListTitle.setText(dataSnapshot.getValue(Event.class).getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error while retriving", Toast.LENGTH_SHORT).show();
                    }
                });
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
                        Intent intent = new Intent(getActivity(), ListDisplayActivity.class);
                        intent.putExtra("type", "event");
                        intent.putExtra("title", getRef(viewHolder.getAdapterPosition()).getKey());
                        startActivity(intent);
                    }
                });
                viewHolder.personalListEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ListCreationActivity.class);
                        intent.putExtra(mListTitle, getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("type", "event");
                        startActivity(intent);
                    }
                });
                viewHolder.personalListDeleteText.setText("Event Info");
                viewHolder.personalListDeleteIcon.setImageResource(R.drawable.ic_info_black_24dp);
                viewHolder.personalListDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child(Constants.events).child(getRef(viewHolder.getAdapterPosition()).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                Intent intent = new Intent(getActivity(), EventDisplayActivity.class);
                                intent.putExtra("event", event);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        eventListsRecyclerViewOwner.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventListsRecyclerViewOwner.setAdapter(ownerlistsAdapter);


        memberlistsAdapter = new FirebaseRecyclerAdapter<Object, PersonalListsViewHolder>(Object.class, R.layout.card_list, PersonalListsViewHolder.class, memberList) {
            @Override
            protected void populateViewHolder(final PersonalListsViewHolder viewHolder, Object model, final int position) {
                Log.d("Event_List_ids", getRef(position).getKey());
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(Constants.events).child(getRef(position).getKey());
                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.personalListTitle.setText(dataSnapshot.getValue(Event.class).getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error while retriving", Toast.LENGTH_SHORT).show();
                    }
                });

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
                        Intent intent = new Intent(getActivity(), ListDisplayActivity.class);
                        intent.putExtra("type", "event");
                        intent.putExtra("title", getRef(viewHolder.getAdapterPosition()).getKey());
                        startActivity(intent);
                    }
                });
                viewHolder.personalListEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ListCreationActivity.class);
                        intent.putExtra(mListTitle,getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("type", "event");
                        startActivity(intent);
                    }
                });

                viewHolder.personalListDeleteText.setText("Event Info");
                viewHolder.personalListDeleteIcon.setImageResource(R.drawable.ic_info_black_24dp);
                viewHolder.personalListDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child(Constants.events).child(getRef(viewHolder.getAdapterPosition()).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                Intent intent = new Intent(getActivity(), EventDisplayActivity.class);
                                intent.putExtra("event", event);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };
        eventListsRecyclerViewMember.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventListsRecyclerViewMember.setAdapter(memberlistsAdapter);

        return v;
    }

    private void clicked(PersonalListsViewHolder viewHolder) {
        if(lastViewHolder != null) {
            lastViewHolder.personalListActions.setVisibility(View.GONE);
        }
        lastViewHolder = viewHolder;
    }
}
