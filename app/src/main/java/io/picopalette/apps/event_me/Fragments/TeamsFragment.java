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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Activities.ListCreationActivity;
import io.picopalette.apps.event_me.Activities.ListDisplayActivity;
import io.picopalette.apps.event_me.Activities.PeopleSearchActivity;
import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Adapters.PersonalListsViewHolder;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

/**
 * Created by Aswin Sundar on 24-06-2017.
 */

public class TeamsFragment extends Fragment {

    private RecyclerView teamsRecView;
    private FirebaseRecyclerAdapter<Object, PersonalListsViewHolder> recyclerAdapter;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(Constants.users);
    private DatabaseReference favRef = userRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favTeams);
    private PersonalListsViewHolder lastViewHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teams,container,false);
        teamsRecView = (RecyclerView) view.findViewById(R.id.teams_recView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                viewHolder.personalListEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PeopleSearchActivity.class);
                        intent.putExtra("teamName",getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("title", getRef(viewHolder.getAdapterPosition()).getKey());
                        intent.putExtra("jobFor", "Teams Fragment");
                        intent.putExtra("job", "edit");
                        startActivity(intent);
                    }
                });
                viewHolder.personalListDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                        builder = new AlertDialog.Builder(getActivity());
//                        }
                        builder.setTitle("Delete Team")
                                .setMessage("Team will be Deleted Permanently")
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
