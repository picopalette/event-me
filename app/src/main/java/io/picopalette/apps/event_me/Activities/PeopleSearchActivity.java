package io.picopalette.apps.event_me.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class PeopleSearchActivity extends AppCompatActivity {

    private SearchView peopleSearchView;
    private RecyclerView peopleRecView;
    private FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder> recyclerAdapter;
    private FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder> teamMembersAdapter;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String jobFor;
    private String job;
    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_search);
        jobFor = getIntent().getStringExtra("jobFor");
        job = getIntent().getStringExtra("job");
        if(jobFor.matches("Teams Fragment")) {
            teamName = getIntent().getStringExtra("teamName");
        }
        peopleRecView = (RecyclerView) findViewById(R.id.people_search_RecView);
        peopleSearchView = (SearchView) findViewById(R.id.people_searchView);
        peopleRecView.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseReference usersRef = dbRef.child(Constants.users);


        if(jobFor.matches("People Fragment")) {

            final DatabaseReference favRef = usersRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favContacts);

            peopleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d("SearchView", "Text changed" + newText);
                    if (newText.matches("")) {
                        peopleRecView.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    peopleRecView.setVisibility(View.VISIBLE);
                    Query nameQuery = usersRef.orderByChild("email").startAt(newText).endAt(newText + "\uf8ff");
                    recyclerAdapter = new FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder>(User.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, nameQuery) {
                        @Override
                        protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, final User model, int position) {
                            viewHolder.nameView.setText(model.getDisplayName());
                            viewHolder.emailView.setText(model.getEmail());
                            Glide.with(PeopleSearchActivity.this)
                                    .load(model.getDpUrl())
                                    .into(viewHolder.imageView);
                            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference favRef = usersRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favContacts);
                                    String newFavKey = favRef.push().getKey();
                                    favRef.child(newFavKey).setValue(model.getEmail());
                                    Toast.makeText(PeopleSearchActivity.this, "Added " + model.getDisplayName() + " to Favorites", Toast.LENGTH_SHORT).show();
                                    viewHolder.addButton.setText("Added");
                                    viewHolder.addButton.setOnClickListener(null);
                                    viewHolder.addButton.setEnabled(false);
                                }
                            });
                            favRef.orderByValue().equalTo(model.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.addButton.setVisibility(View.VISIBLE);
                                    if (dataSnapshot.hasChildren()) {
                                        viewHolder.addButton.setText("Cool!");
                                        viewHolder.addButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    };

                    peopleRecView.setAdapter(recyclerAdapter);
                    return true;
                }
            });
        } else if(jobFor.matches("Teams Fragment")) {

            final DatabaseReference teamRef = dbRef.child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favTeams).child(teamName);

            if(job.matches("view")) {
                peopleSearchView.setVisibility(View.GONE);
            }

            teamMembersAdapter = new FirebaseRecyclerAdapter<String, PeopleSearchCardViewHolder>(String.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, teamRef) {
                @Override
                protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, String model, final int position) {
                    dbRef.child(Constants.users).child(Utilities.encodeEmail(model)).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            viewHolder.nameView.setText(name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dbRef.child(Constants.users).child(Utilities.encodeEmail(model)).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String email = dataSnapshot.getValue(String.class);
                            viewHolder.emailView.setText(email);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dbRef.child(Constants.users).child(Utilities.encodeEmail(model)).child("dpUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String dpUrl = dataSnapshot.getValue(String.class);
                            Glide.with(PeopleSearchActivity.this)
                                    .load(dpUrl)
                                    .into(viewHolder.imageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if(job.matches("edit")) {
                        viewHolder.addButton.setVisibility(View.VISIBLE);
                        viewHolder.addButton.setEnabled(true);
                        viewHolder.addButton.setText("Remove");
                        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                                builder = new AlertDialog.Builder(PeopleSearchActivity.this);
//                        }
                                builder.setTitle("Remove Member")
                                        .setMessage(viewHolder.nameView.getText().toString()+" will be removed from team")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                                getRef(position).removeValue();
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

            peopleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d("SearchView", "Text changed" + newText);
                    if (newText.matches("")) {
                        peopleRecView.setAdapter(teamMembersAdapter);
                        return true;
                    }
                    peopleRecView.setVisibility(View.VISIBLE);
                    Query nameQuery = usersRef.orderByChild("email").startAt(newText).endAt(newText + "\uf8ff");
                    recyclerAdapter = new FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder>(User.class, R.layout.card_people_search, PeopleSearchCardViewHolder.class, nameQuery) {
                        @Override
                        protected void populateViewHolder(final PeopleSearchCardViewHolder viewHolder, final User model, int position) {
                            viewHolder.nameView.setText(model.getDisplayName());
                            viewHolder.emailView.setText(model.getEmail());
                            Glide.with(PeopleSearchActivity.this)
                                    .load(model.getDpUrl())
                                    .into(viewHolder.imageView);
                            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newFavKey = teamRef.push().getKey();
                                    teamRef.child(newFavKey).setValue(model.getEmail());
                                    Toast.makeText(PeopleSearchActivity.this, "Added " + model.getDisplayName() + " to Favorites", Toast.LENGTH_SHORT).show();
                                    viewHolder.addButton.setText("Added");
                                    viewHolder.addButton.setOnClickListener(null);
                                    viewHolder.addButton.setEnabled(false);
                                }
                            });
                            teamRef.orderByValue().equalTo(model.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.addButton.setVisibility(View.VISIBLE);
                                    if (dataSnapshot.hasChildren()) {
                                        viewHolder.addButton.setText("Cool!");
                                        viewHolder.addButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    };

                    peopleRecView.setAdapter(recyclerAdapter);
                    return true;
                }
            });

            peopleRecView.setAdapter(teamMembersAdapter);

        }
    }

}
