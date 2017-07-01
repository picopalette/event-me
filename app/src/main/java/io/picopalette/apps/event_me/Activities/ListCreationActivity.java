package io.picopalette.apps.event_me.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.picopalette.apps.event_me.Adapters.PersonalListEditViewHolder;
import io.picopalette.apps.event_me.Models.ListItem;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class ListCreationActivity extends AppCompatActivity {

    EditText mListItem;
    FloatingActionButton mAddFAB;
    TextView mTitle;
    RecyclerView mRecyclerView;
    private DatabaseReference mListReference;
    private String title;
    private String type;
    private FirebaseRecyclerAdapter editAdapter;
    public static final String mListTitle = "List Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_creation);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_creation_RecyclerView);
        mListItem = (EditText) findViewById(R.id.listEditText);
        mAddFAB = (FloatingActionButton) findViewById(R.id.addFAB);
        mTitle = (TextView) findViewById(R.id.titleTextView);

        Intent intent = getIntent();
        title = intent.getStringExtra(mListTitle);
        type = intent.getStringExtra("type");

        if(type.matches("personal")) {
            mListReference = FirebaseDatabase.getInstance().getReference().child(Constants.users).child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child(Constants.lists).child(title);
            mTitle.setText(title);
        } else {
            mListReference = FirebaseDatabase.getInstance().getReference().child(Constants.events).child(title).child(Constants.lists);
            FirebaseDatabase.getInstance().getReference().child(Constants.events).child(title).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mTitle.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mTitle.setText(title);
                }
            });
        }

        mAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListItem.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"No input given to ADD", Toast.LENGTH_LONG).show();
                }
                else
                {
                    String newItemString = mListItem.getText().toString();
                    String itemId = mListReference.push().getKey();
                    ListItem newItem = new ListItem(itemId, newItemString, false);
                    mListReference.child(itemId).setValue(newItem);
                    mListItem.setText("");
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        editAdapter = new FirebaseRecyclerAdapter<ListItem, PersonalListEditViewHolder>(ListItem.class, R.layout.list_item_edit, PersonalListEditViewHolder.class, mListReference) {

            @Override
            protected void populateViewHolder(PersonalListEditViewHolder viewHolder, final ListItem model, int position) {
                viewHolder.listTextView.setText(model.getItemName());
                viewHolder.listDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListReference.child(model.getId()).removeValue();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(editAdapter);

    }
//
//    private void getDataTask() {
//        mListReference.child(title).keepSynced(true);
//        mListReference.child(title).addValueEventListener(
//                new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
//                    ListItem listItem = listSnapshot.getValue(ListItem.class);
//                    if(!editAdapter.list_item_edit.contains(listItem)) {
//                        editAdapter.list_item_edit.add(listItem);
//                        editAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}
