package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.picopalette.apps.event_me.Interfaces.RecyclerViewReadyCallback;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

/**
 * Created by Aswin Sundar on 08-07-2017.
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> implements RecyclerViewReadyCallback {

    public List<Event> events;
    private int keep = 0;
    public View itemView;
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerViewReadyCallback recyclerViewReadyCallback;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public TimelineAdapter(Context context, List<Event> events,RecyclerView recyclerView){
        this.events = events;
        this.context = context;
        this.recyclerView = recyclerView;
    }


    @Override
    public TimelineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_timeline_item, parent, false);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (recyclerViewReadyCallback != null) {
                    recyclerViewReadyCallback.onLayoutReady();
                }
                recyclerViewReadyCallback = null;
            }
        });
        recyclerViewReadyCallback  = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                //Perform Sorting
                if (keep != events.size())
                {
                    keep = events.size();
                    Log.d("sizes", "keep: "+keep+"events.size"+ events.size());
                    for(int i = 0; i<keep; i++)
                    {
                        for(int j = i+1; j< keep; j++)
                        {
                            Event homeeve = events.get(i);
                            Date time1 = null;
                            time1 = new Date(homeeve.getDateAndTime().getYear(), homeeve.getDateAndTime().getMonth(), homeeve.getDateAndTime().getDayOfMonth(), homeeve.getDateAndTime().getHourOfDay(), homeeve.getDateAndTime().getMinute());
                            Event homeeve2 = events.get(j);
                            Date time2 = null;
                            time2 = new Date(homeeve2.getDateAndTime().getYear(), homeeve2.getDateAndTime().getMonth(), homeeve2.getDateAndTime().getDayOfMonth(), homeeve2.getDateAndTime().getHourOfDay(), homeeve2.getDateAndTime().getMinute());
                            if(time1.before(time2)) {
                                Event itemA = events.get(i);
                                Event itemB = events.get(j);
                                events.set(i, itemB);
                                events.set(j, itemA);
                                Log.d("sorting time1", time1.toString());
                                Log.d("sorting time2", time2.toString());
                                Log.d("sorted events", events.get(0).toString());
                            }
                        }
                    }

                    notifyDataSetChanged();

                }
            }
        };


        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(final TimelineAdapter.MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Log.d("TESTI", "inside bindviewholder");
        final Event homeEvent = events.get(holder.getAdapterPosition());
        holder.timeEventName.setText(homeEvent.getName());
        holder.timeEventPlace.setText(homeEvent.getPlace().getName());
        holder.timeEventHashTag.setText(homeEvent.getType());
        holder.timeEventTime.setText(homeEvent.getDateAndTime().getFormattedTime());
        holder.timeEventDay.setText(homeEvent.getDateAndTime().getDayOfMonth());
        holder.timeEventMonth.setText(homeEvent.getDateAndTime().getMonth());
        holder.timeEventYear.setText(homeEvent.getDateAndTime().getYear());
        holder.timeEventCount.setText(homeEvent.getParticipants().size());

        storageRef.child("images/" + homeEvent.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri.toString())
                        .into(holder.timeEventPic);
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

    @Override
    public void onLayoutReady() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timeEventName, timeEventPlace, timeEventHashTag, timeEventMonth, timeEventDay, timeEventYear, timeEventTime,
                timeEventCount;
        ImageView timeEventPic;

        public MyViewHolder(View itemView) {

            super(itemView);

            timeEventName = (TextView) itemView.findViewById(R.id.timelineEventsName);
            timeEventPlace = (TextView) itemView.findViewById(R.id.timelineEventPlace);
            timeEventHashTag = (TextView) itemView.findViewById(R.id.timelineHashTag);
            timeEventDay = (TextView) itemView.findViewById(R.id.timelineEventDate);
            timeEventMonth = (TextView) itemView.findViewById(R.id.timelineEventMonth);
            timeEventYear = (TextView) itemView.findViewById(R.id.timelineEventYear);
            timeEventTime = (TextView) itemView.findViewById(R.id.timelineEventTime);
            timeEventCount = (TextView) itemView.findViewById(R.id.timelineParticipantsCount);

            timeEventPic = (ImageView) itemView.findViewById(R.id.timelineImage);

        }
    }
}
