package io.picopalette.apps.event_me.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import io.picopalette.apps.event_me.Adapters.SearchViewHolder;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
public class EventSearchActivity extends Activity{

    private DatabaseReference databaseReference;
    private RecyclerView searchRecycler;
    private FirebaseRecyclerAdapter<Event,SearchViewHolder> searchAdapter;
    private GridLayoutManager mLayoutManager;
    private StorageReference storageRef;
    private Uri myuri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay_view);
        storageRef = FirebaseStorage.getInstance().getReference();
        searchRecycler = (RecyclerView) findViewById( R.id.searchRecycler);
        searchRecycler.setHasFixedSize( true );
        mLayoutManager = new GridLayoutManager(this, 2);
        searchRecycler.setLayoutManager(mLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child( Constants.events );
        Log.d("before ordereing", databaseReference.toString());
        final Query query = databaseReference.orderByChild("private").equalTo(false);

        searchAdapter = new FirebaseRecyclerAdapter<Event, SearchViewHolder>(Event.class,R.layout.searchview_card,SearchViewHolder.class,query) {

            @Override
            protected void populateViewHolder(final SearchViewHolder viewHolder, final Event model, int position) {
                Log.d("after ordereing", query.toString());
                    viewHolder.search.setText( model.getName() );
                    storageRef.child("images/" + model.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(EventSearchActivity.this)
                                    .load(uri).into(viewHolder.imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {


                        }

                    });
                    Log.d( "eventsss", model.getName() + "\n" );
                viewHolder.imageView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), EventDisplayActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
                        intent.putExtra("event", model);
                        startActivity(intent);
                    }
                } );
            }
        };


        searchRecycler.setAdapter( searchAdapter );

    }

    private Uri getBitmap(Event mymodel) {
        storageRef.child("images/" + mymodel.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                myuri = uri;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                myuri = null;

            }

        });
        return  myuri;

    }


}
