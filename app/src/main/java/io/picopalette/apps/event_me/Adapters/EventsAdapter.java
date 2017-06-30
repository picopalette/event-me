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
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Activities.EventDisplayActivity;
import io.picopalette.apps.event_me.Interfaces.RecyclerViewReadyCallback;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> implements RecyclerViewReadyCallback{

    public List<Event> events;
    private int keep = 0;
    public View itemView;
    private Context context;
    private RecyclerView recyclerView;
    private SimpleDateFormat sdf;
    private String currentTime;
    private RecyclerViewReadyCallback recyclerViewReadyCallback;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public EventsAdapter(Context context, List<Event> events,RecyclerView recyclerView)  {
        this.events = events;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


             itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_tab_custom_row, parent, false);

            Calendar c = Calendar.getInstance();
            sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
            currentTime = sdf.format(c.getTime());

            //Perform Sorting
//            if (keep < events.size()) {
//                keep = events.size();
//
//                Log.d("sizes", "keep: " + keep + "events.size" + events.size());
//                for (int i = 0; i < keep; i++) {
//                    final Event homeeve = events.get(i);
//                    Date time1 = null;
//                    try {
//                        time1 = sdf.parse(homeeve.getDateAndTime().getFormattedDate() + " " + homeeve.getDateAndTime().getFormattedTime());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    for (int j = i + 1; j < keep; j++) {
//                        final Event homeeve2 = events.get(j);
//                        Date time2 = null;
//                        try {
//                            time2 = sdf.parse(homeeve2.getDateAndTime().getFormattedDate() + " " + homeeve2.getDateAndTime().getFormattedTime());
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.d("sorting", "time1: " + time1 + " time2: " + time2 + " result: " + time1.compareTo(time2));
//                        if (time1.compareTo(time2) > 0) {
//                            Event itemA = events.get(i);
//                            Event itemB = events.get(j);
//                            events.set(i, itemB);
//                            events.set(j, itemA);
//                        }
//                    }
//                }
//
//                // notifyDataSetChanged();
//
//            }

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
                if (keep < events.size())
                {
                    keep = events.size();

                    Log.d("sizes", "keep: "+keep+"events.size"+ events.size());
                    for(int i = 0; i<keep; i++)
                    {
                        final Event homeeve = events.get(i);
                        Date time1 = null;
                        try {
                            time1 = sdf.parse(homeeve.getDateAndTime().getFormattedDate()+" "+homeeve.getDateAndTime().getFormattedTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        for(int j = i+1; j< keep; j++)
                        {
                            final Event homeeve2 = events.get(j);
                            Date time2 = null;
                            try {
                                time2 = sdf.parse(homeeve2.getDateAndTime().getFormattedDate()+" "+homeeve2.getDateAndTime().getFormattedTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Log.d("sorting","time1: "+time1+" time2: "+time2+" result: "+time1.compareTo(time2));
                            if(time1.compareTo(time2)>0){
                                Event itemA = events.get(i);
                                Event itemB = events.get(j);
                                events.set(i, itemB);
                                events.set(j, itemA);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            Log.d("TESTI", "inside bindviewholder");
            final Event homeEvent = events.get(holder.getAdapterPosition());
            holder.event_title.setText(homeEvent.getName());
            holder.event_type.setText(homeEvent.getType());
            holder.event_date_time.setText(homeEvent.getDateAndTime().getFormattedDate() + " " + homeEvent.getDateAndTime().getFormattedTime());
            holder.event_place.setText(homeEvent.getPlace().getName());
            storageRef.child("images/" + homeEvent.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri.toString())
                            .into(holder.event_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context,"cannot create your Event",Toast.LENGTH_LONG).show();
                }
            });



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Event homeEvent = events.get(holder.getAdapterPosition());

                    Intent intent = new Intent(context, EventDisplayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
                    intent.putExtra("event", homeEvent);
                    context.startActivity(intent);

                }
            });



    }

    @Override
    public int getItemCount() {
        return (null != events ? events.size() : 0);
    }

    @Override
    public void onLayoutReady() {
        //Perform Sorting
        if (keep < events.size())
        {
            keep = events.size();

            Log.d("sizes", "keep: "+keep+"events.size"+ events.size());
            for(int i = 0; i<keep; i++)
            {
                final Event homeeve = events.get(i);
                Date time1 = null;
                try {
                    time1 = sdf.parse(homeeve.getDateAndTime().getFormattedDate()+" "+homeeve.getDateAndTime().getFormattedTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for(int j = i+1; j< keep; j++)
                {
                    final Event homeeve2 = events.get(j);
                    Date time2 = null;
                    try {
                        time2 = sdf.parse(homeeve2.getDateAndTime().getFormattedDate()+" "+homeeve2.getDateAndTime().getFormattedTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("sorting","time1: "+time1+" time2: "+time2+" result: "+time1.compareTo(time2));
                    if(time1.compareTo(time2)>0){
                        Event itemA = events.get(i);
                        Event itemB = events.get(j);
                        events.set(i, itemB);
                        events.set(j, itemA);
                    }
                }
            }

            notifyDataSetChanged();

        }

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event_title, event_type, event_place, event_date_time;
        CircleImageView event_image;
        TextView accept, decline, notificationText;

        MyViewHolder(View view) {
            super(view);
            notificationText = (TextView) view.findViewById(R.id.notfication_tv);
            accept = (TextView) view.findViewById(R.id.accept_tv);
            decline = (TextView) view.findViewById(R.id.decline_tv);
            event_title = (TextView) view.findViewById(R.id.event_name_card);
            event_type = (TextView) view.findViewById(R.id.event_type_card);
            event_place = (TextView) view.findViewById(R.id.event_place_card);
            event_date_time = (TextView) view.findViewById(R.id.event_date_time_card);
            event_image = (CircleImageView) view.findViewById(R.id.event_image_card);
        }

    }



}