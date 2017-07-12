package io.picopalette.apps.event_me.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.picopalette.apps.event_me.Adapters.PeopleSearchCardViewHolder;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class PeopleSearchActivity extends AppCompatActivity {

    private SearchView peopleSearchView;
    private RecyclerView peopleRecView;
    private FirebaseRecyclerAdapter<User, PeopleSearchCardViewHolder> recyclerAdapter;
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.users);
    private ArrayList<String> alreadyFav = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_search);
        peopleRecView = (RecyclerView) findViewById(R.id.people_search_RecView);
        peopleSearchView = (SearchView) findViewById(R.id.people_searchView);
        peopleRecView.setLayoutManager(new LinearLayoutManager(this));



        final DatabaseReference favRef = usersRef.child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.favContacts);

        peopleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.matches("")) {
                    peopleRecView.setVisibility(View.INVISIBLE);
                    return true;
                }
                peopleRecView.setVisibility(View.VISIBLE);
                Query nameQuery = usersRef.orderByChild("email").startAt(query).endAt(query + "\uf8ff");
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
                                viewHolder.addButton.setText("Added");
                                viewHolder.addButton.setOnClickListener(null);
                                viewHolder.addButton.setEnabled(false);
                            }
                        });
                        favRef.orderByValue().equalTo(model.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren()) {
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

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchView", "Text changed" + newText);
                if(newText.matches("")) {
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
                                viewHolder.addButton.setText("Added");
                                viewHolder.addButton.setOnClickListener(null);
                                viewHolder.addButton.setEnabled(false);
                            }
                        });
                        favRef.orderByValue().equalTo(model.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren()) {
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
    }

}
