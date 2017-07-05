package io.picopalette.apps.event_me.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Adapters.PersonalListViewViewHolder;
import io.picopalette.apps.event_me.Models.ListItem;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class ListDisplayActivity extends AppCompatActivity {

    private String type;
    private String title;
    private RecyclerView listDisplayRecyclerView;
    private FirebaseRecyclerAdapter<ListItem, PersonalListViewViewHolder> viewAdapter;
    private DatabaseReference mRef;
    private TextView listDisplayTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");
        listDisplayTitleView = (TextView) findViewById(R.id.list_dislay_title_TextView);
        listDisplayRecyclerView = (RecyclerView) findViewById(R.id.list_display_RecyclerView);
        listDisplayRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRef = FirebaseDatabase.getInstance().getReference();
        if(type.matches("personal")) {
            listDisplayTitleView.setText(title);
            mRef = mRef.child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.lists).child(title);
        }
        else {
            mRef = mRef.child(Constants.events).child(title).child(Constants.lists);
            FirebaseDatabase.getInstance().getReference().child(Constants.events).child(title).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listDisplayTitleView.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listDisplayTitleView.setText(title);
                }
            });
        }
        mRef.keepSynced(true);
        viewAdapter = new FirebaseRecyclerAdapter<ListItem, PersonalListViewViewHolder>(ListItem.class, R.layout.list_item_view, PersonalListViewViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(final PersonalListViewViewHolder viewHolder, final ListItem model, final int position) {
                viewHolder.listItemCheckedTextView.setText(model.getItemName());
                if(model.getIsDone()) {
                    viewHolder.listItemCheckedTextView.setChecked(true);
                }
                else {
                    viewHolder.listItemCheckedTextView.setChecked(false);
                }
                viewHolder.listItemCheckedTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.listItemCheckedTextView.isChecked()) {
                            getRef(position).child("isDone").setValue(false);
                            viewAdapter.notifyItemChanged(position);
                        }
                        else {
                            getRef(position).child("isDone").setValue(true);
                            viewAdapter.notifyItemChanged(position);
                        }
                    }
                });
            }
        };
        listDisplayRecyclerView.setAdapter(viewAdapter);
    }
}
