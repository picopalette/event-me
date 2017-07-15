package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Activities.PeopleSearchActivity;
import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 15-07-2017.
 */

public class EventParFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private CardView cardView;
    private FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder> recyclerAdapter;
    private FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder> searchAdapter;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.people_recView);
        searchView = (SearchView) view.findViewById(R.id.people_searchView1);
        cardView = (CardView) view.findViewById(R.id.search_back_card1);
        cardView.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final String eventId = getActivity().getIntent().getStringExtra("eventId");
        final DatabaseReference partRef = dbRef.child(Constants.events).child(eventId).child("participants");
        final DatabaseReference usersRef = dbRef.child(Constants.users);
        final DatabaseReference eventLiveParRef = dbRef.child(Constants.events).child(getActivity().getIntent().getStringExtra("eventId")).child("liveparticipants");


        recyclerAdapter = new FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder>(String.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, partRef) {
            @Override
            protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, String model, int position) {
                final String email = getRef(viewHolder.getAdapterPosition()).getKey();
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
                                            usersRef.child(email).child(Constants.events).child(eventId).removeValue();
                                            eventLiveParRef.child(email).removeValue();
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


        searchView.setQueryHint("Search for you Friends");
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.matches("")) {
                    recyclerView.setAdapter(recyclerAdapter);
                    return true;
                }
                Query nameQuery = usersRef.orderByChild("email").startAt(newText).endAt(newText + "\uf8ff");
                searchAdapter = new FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder>(User.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, nameQuery) {
                    @Override
                    protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, final User model, int position) {
                        viewHolder.nameView.setText(model.getDisplayName());
                        viewHolder.emailView.setText(model.getEmail());
                        Glide.with(getActivity())
                                .load(model.getDpUrl())
                                .into(viewHolder.imageView);
                        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                partRef.child(Utilities.encodeEmail(model.getEmail())).setValue(Constants.UserStatus.INVITED);
                                eventLiveParRef.child(Utilities.encodeEmail(model.getEmail())).setValue(false);
                                usersRef.child(Utilities.encodeEmail(model.getEmail())).child(Constants.events).child(eventId).setValue(Constants.UserStatus.INVITED);
                                Toast.makeText(getActivity(), "Added " + model.getDisplayName() + " to Event", Toast.LENGTH_SHORT).show();
                                viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                                searchView.setQuery("", false);
                                viewHolder.addButton.setOnClickListener(null);
                                viewHolder.addButton.setEnabled(false);
                            }
                        });
                        partRef.orderByKey().equalTo(Utilities.encodeEmail(model.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                viewHolder.addButton.setVisibility(View.VISIBLE);
                                if (dataSnapshot.hasChildren()) {
                                    viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_blue_24dp));
                                    viewHolder.addButton.setEnabled(false);
                                    viewHolder.addButton.setOnClickListener(null);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if(model.getEmail().matches(currentUser.getEmail())) {
                            viewHolder.addButton.setVisibility(View.INVISIBLE);
                            viewHolder.addButton.setOnClickListener(null);
                        }
                    }
                };

                recyclerView.setAdapter(searchAdapter);
                return true;
            }
        });

    }
}
