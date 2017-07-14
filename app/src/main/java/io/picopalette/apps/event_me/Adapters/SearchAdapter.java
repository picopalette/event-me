package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

/**
 * Created by holmesvinn on 14/7/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{
    private View itemView2;
    private Context context;
    private RecyclerView searchrecycler;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    public List<Event> events;

    public SearchAdapter(Context applicationContext, List<Event> events, RecyclerView searchRecycler) {
        this.context = applicationContext;
        this.events = events;
        this.searchrecycler = searchRecycler;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView2 = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.searchview_card, parent, false);
        return new MyViewHolder(itemView2);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Log.d("TESTI34", "inside bindviewholder");
        final Event homeEvent = events.get(holder.getAdapterPosition());
        holder.textView.setText( homeEvent.getName() );
        storageRef.child("images/" + homeEvent.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri.toString())
                        .into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != events ? events.size() : 0);
    }

    public void setfilter(ArrayList<Event> newevelist){
        events = new ArrayList<>();
        events.addAll( newevelist );
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CircleImageView imageView;
        public MyViewHolder(View itemView) {
            super( itemView );
            textView = (TextView) itemView.findViewById( R.id.searchimageText );
            imageView = (CircleImageView) itemView.findViewById( R.id.searchImage );
        }
    }
}
