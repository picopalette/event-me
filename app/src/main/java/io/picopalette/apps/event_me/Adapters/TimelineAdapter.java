package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.timeline.TimelineView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mzelzoghbi.zgallery.entities.ZColor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.picopalette.apps.event_me.Interfaces.RecyclerViewReadyCallback;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.EventGallery;
import io.picopalette.apps.event_me.Utils.Utilities;

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


        return new MyViewHolder(itemView, viewType);


    }

    @Override
    public void onBindViewHolder(final TimelineAdapter.MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Log.d("TESTI", "inside bindviewholder");
        final Event homeEvent = events.get(holder.getAdapterPosition());
        Log.d("TESTI", String.valueOf(homeEvent.getDateAndTime().getYear()));
        holder.timeEventName.setText(homeEvent.getName());
        holder.timeEventPlace.setText(homeEvent.getPlace().getName());
        holder.timeEventHashTag.setText(homeEvent.getType());
        ArrayList<String> splittedTime = homeEvent.getDateAndTime().getSplitTime();
        holder.timeEventHours.setText(splittedTime.get(0));
        holder.timeEventMinutes.setText(splittedTime.get(1));
        holder.timeEventMeridian.setText(splittedTime.get(2));
        holder.timeEventDay.setText(String.valueOf(homeEvent.getDateAndTime().getDayOfMonth()));
        int monthTimeline = homeEvent.getDateAndTime().getMonth() + 1;
        holder.timeEventMonth.setText(Utilities.monthFormatter(monthTimeline));
        holder.timeEventYear.setText(String.valueOf(homeEvent.getDateAndTime().getYear()));
        holder.timeEventCount.setText(String.valueOf(homeEvent.getParticipants().size()));
        holder.timeline.setTimelineType(TimelineView.TYPE_DEFAULT);
        holder.timeline.setTimelineAlignment(TimelineView.ALIGNMENT_DEFAULT);
        holder.participantsRecView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<String> partiEmails = new ArrayList<>();
        for(String email : homeEvent.getParticipants().keySet())
            partiEmails.add(email);
        holder.participantsRecView.setAdapter(new TimelinePersonsAdapter(context, partiEmails));

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

        holder.viewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventGallery.with(context, homeEvent.getId())
                        .setToolbarTitleColor(ZColor.WHITE) // toolbar title color
                        .setGalleryBackgroundColor(ZColor.WHITE) // activity background color
                        .setToolbarColorResId(R.color.colorPrimary) // toolbar color
                        .setTitle(homeEvent.getName()) // toolbar title
                        .show();
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

        TextView timeEventName, timeEventPlace, timeEventHashTag, timeEventMonth, timeEventDay, timeEventYear,
                timeEventCount, timeEventHours, timeEventMinutes, timeEventMeridian;
        ImageView timeEventPic;
        RecyclerView participantsRecView;
        TimelineView timeline;
        LinearLayout viewGallery;


        public MyViewHolder(View itemView, int viewType) {

            super(itemView);
            timeEventName = (TextView) itemView.findViewById(R.id.timelineEventsName);
            timeEventPlace = (TextView) itemView.findViewById(R.id.timelineEventPlace);
            timeEventHashTag = (TextView) itemView.findViewById(R.id.timelineHashTag);
            timeEventDay = (TextView) itemView.findViewById(R.id.timelineEventDate);
            timeEventMonth = (TextView) itemView.findViewById(R.id.timelineEventMonth);
            timeEventYear = (TextView) itemView.findViewById(R.id.timelineEventYear);
            timeEventHours = (TextView) itemView.findViewById(R.id.timelineHours);
            timeEventMeridian = (TextView) itemView.findViewById(R.id.timelineMeridian);
            timeEventMinutes = (TextView) itemView.findViewById(R.id.timelineMinutes);
            timeEventCount = (TextView) itemView.findViewById(R.id.timelineParticipantsCount);
            timeEventPic = (ImageView) itemView.findViewById(R.id.timelineImage);
            participantsRecView = (RecyclerView) itemView.findViewById(R.id.timelineParticipantsRecView);
            timeline = (TimelineView) itemView.findViewById(R.id.timeline1);
            viewGallery = (LinearLayout) itemView.findViewById(R.id.timelineViewGallery);

        }
    }
}
