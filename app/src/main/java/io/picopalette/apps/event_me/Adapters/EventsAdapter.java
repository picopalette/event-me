package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Activities.LiveShare;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> implements View.OnClickListener {

    public List<Event> events;
    private Context context;
    private Double lat, lon;
    private String key;
    private View itemView;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public EventsAdapter(Context context, List<Event> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_tab_custom_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.d("adapter", events.get(position).toString());
        final Event homeEvent = events.get(position);
        holder.event_title.setText(homeEvent.getName());
        holder.event_type.setText(homeEvent.getType());
        holder.event_date_time.setText(homeEvent.getDateAndTime().getFormattedDate() + " " + homeEvent.getDateAndTime().getFormattedTime());
        holder.event_place.setText(homeEvent.getPlace().getName());
        storageRef.child("images/"+homeEvent.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // TODO: handle uri
                Glide.with(itemView.getContext())
                        .load(uri.toString())
                        .dontAnimate()
                        .into(holder.event_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LiveShare.class);
                intent.putExtra("lat", homeEvent.getPlace().getLat());
                intent.putExtra("lon", homeEvent.getPlace().getLon());
                intent.putExtra("uid", homeEvent.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != events ? events.size() : 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, LiveShare.class);
        context.startActivity(intent);

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event_title, event_type, event_place, event_date_time;
        CircleImageView event_image;

        MyViewHolder(View view) {
            super(view);
            event_title = (TextView) view.findViewById(R.id.event_name_card);
            event_type = (TextView) view.findViewById(R.id.event_type_card);
            event_place = (TextView) view.findViewById(R.id.event_place_card);
            event_date_time = (TextView) view.findViewById(R.id.event_date_time_card);
            event_image = (CircleImageView) view.findViewById(R.id.event_image_card);
        }

    }

}