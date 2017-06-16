package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import io.picopalette.apps.event_me.Activities.LiveShare;
import io.picopalette.apps.event_me.Models.Event;
import io.picopalette.apps.event_me.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> implements View.OnClickListener{

    private List<Event> events;
    private Context context;
    private Double lat, lon;
    private String key;

    public EventsAdapter(Context context, List<Event> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_tab_custom_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event homeEvent = events.get(position);
        holder.event_title.setText(homeEvent.getName());
        holder.event_type.setText(homeEvent.getType());
        holder.event_date_time.setText(homeEvent.getDateAndTime().getFormattedDate() + " " + homeEvent.getDateAndTime().getFormattedTime());
        holder.event_place.setText(homeEvent.getPlace().getName());
        holder.event_image.setBackgroundResource(R.drawable.logo);

        lat = homeEvent.getPlace().getLat();
        lon = homeEvent.getPlace().getLon();
        key = homeEvent.getId();
    }

    @Override
    public int getItemCount() {
        return (null != events ? events.size() : 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context,LiveShare.class);
        context.startActivity(intent);

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView event_title,event_type,event_place,event_date_time;
        ImageView event_image;

        MyViewHolder(View view) {
            super(view);
            event_title = (TextView) view.findViewById(R.id.event_name_card);
            event_type = (TextView) view.findViewById(R.id.event_type_card);
            event_place = (TextView) view.findViewById(R.id.event_place_card);
            event_date_time = (TextView) view.findViewById(R.id.event_date_time_card);
            event_image = (ImageView) view.findViewById(R.id.event_image_card);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            Intent intent = new Intent(context,LiveShare.class);
            intent.putExtra("lat",lat);
            intent.putExtra("lon",lon);
            intent.putExtra("uid",key);
            context.startActivity(intent);


        }
    }

}