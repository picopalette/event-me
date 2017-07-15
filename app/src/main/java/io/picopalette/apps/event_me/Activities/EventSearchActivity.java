package io.picopalette.apps.event_me.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.picopalette.apps.event_me.Adapters.EventsAdapter;
import io.picopalette.apps.event_me.Adapters.SearchAdapter;
import io.picopalette.apps.event_me.Adapters.SearchViewHolder;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class EventSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private DatabaseReference databaseReference;
    private RecyclerView searchRecycler;
    private GridLayoutManager mLayoutManager;
    private ArrayList<Event> events;
    private SearchAdapter adapter;
    private List<Event> homeevents;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.overlay_view );
        homeevents = (List<Event>) getIntent().getExtras().get( "mylist" );
        getmydata( homeevents);
        searchRecycler = (RecyclerView) findViewById( R.id.searchRecycler );
        events = new ArrayList<>();
        searchRecycler.setHasFixedSize( true );
        adapter = new SearchAdapter(getApplicationContext(), events, searchRecycler, this );
        searchRecycler.setAdapter( adapter );
        mLayoutManager = new GridLayoutManager( this, 2 );
        searchRecycler.setLayoutManager( mLayoutManager );



//        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.events);
//        Log.d("before ordereing", databaseReference.toString());
//        final Query query = databaseReference.orderByChild("private").equalTo(false);
//
//        searchAdapter = new FirebaseRecyclerAdapter<Event, SearchViewHolder>(Event.class,R.layout.searchview_card,SearchViewHolder.class,query) {
//
//            @Override
//            protected void populateViewHolder(final SearchViewHolder viewHolder, final Event model, int position) {
//
//                Log.d("after ordereing", String.valueOf( arrayList ) );
//                    viewHolder.search.setText( model.getName() );
//                    storageRef.child("images/" + model.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Picasso.with(EventSearchActivity.this)
//                                    .load(uri).into(viewHolder.imageView);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//
//
//                        }
//
//                    });
//                    Log.d( "eventsss", model.getName() + "\n" );
//                final Event finalModel = model;
//                viewHolder.imageView.setOnClickListener( new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getBaseContext(), EventDisplayActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
//                        intent.putExtra("event", model );
//                        startActivity(intent);
//                    }
//                } );
//            }
//        };
//
//
//        searchRecycler.setAdapter( searchAdapter );
//        Log.d("mytesting", String.valueOf( arrayList ) );



    }

    private void getmydata(final List<Event> events) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.events);
        final Query query = databaseReference.orderByChild("private").equalTo(false);
        query.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Log.d("mytestings", String.valueOf( eventSnapshot) );
                    Event event = eventSnapshot.getValue(Event.class);
                    Log.d("mytestings2", String.valueOf(event.getName()) );
                    Log.d("vikkkkky", String.valueOf(events) );
                    if(!events.contains(event) && !adapter.events.contains(event)) {
                        adapter.events.add(event);
                        Log.d("vikky2", String.valueOf(event) );
                    }
                    adapter.notifyDataSetChanged();

                }


                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Event> neweve  =new ArrayList<>();
        for(Event event: events){
            String name  = event.getName().toLowerCase();
            if(name.contains( newText )){
                neweve.add( event );
            }
            adapter.setfilter(neweve);

        }
       return false;
    }
}
