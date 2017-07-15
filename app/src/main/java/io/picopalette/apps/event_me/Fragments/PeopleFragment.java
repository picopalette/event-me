package io.picopalette.apps.event_me.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Activities.MainActivity;
import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 24-06-2017.
 */

public class PeopleFragment extends Fragment {

    private RecyclerView peopleRecView;
    private FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder> recyclerAdapter;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(Constants.users);
    private DatabaseReference favRef = userRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favContacts);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        peopleRecView = (RecyclerView) view.findViewById(R.id.people_recView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerAdapter = new FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder>(String.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, favRef) {
            @Override
            protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, String model, final int position) {
                userRef.child(Utilities.encodeEmail(model)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        viewHolder.nameView.setText(user.getDisplayName());
                        viewHolder.emailView.setText(user.getEmail());
                        Glide.with(getActivity().getApplicationContext())
                                .load(user.getDpUrl())
                                .into(viewHolder.imageView);
                        viewHolder.statusView.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_blue_24dp));
                        viewHolder.addButton.setVisibility(View.VISIBLE);
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
                                        .setMessage(user.getDisplayName() + " will be removed from favourites")
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        peopleRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        peopleRecView.setAdapter(recyclerAdapter);
    }
}
