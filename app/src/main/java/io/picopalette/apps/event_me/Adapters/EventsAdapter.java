package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import io.picopalette.apps.event_me.Datas.EventsData;
import io.picopalette.apps.event_me.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    private List<EventsData> eventsDataList;
    private Context context;

    public EventsAdapter(Context context, List<EventsData> homeEventList) {
        this.eventsDataList = homeEventList;
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
        EventsData homeEvent = eventsDataList.get(position);
        holder.event_title.setText(homeEvent.getEve_home_name());
        holder.event_type.setText(homeEvent.getEve_home_type());
        holder.event_date_time.setText(homeEvent.getEve_home_date_time());
        holder.event_place.setText(homeEvent.getEve_home_place());
        holder.event_image.setBackgroundResource(R.drawable.logo);
    }

    @Override
    public int getItemCount() {
        return (null != eventsDataList ? eventsDataList.size() : 0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView event_title,event_type,event_place,event_date_time;
        ImageView event_image;

        MyViewHolder(View view) {
            super(view);
            event_title = (TextView) view.findViewById(R.id.event_name_card);
            event_type = (TextView) view.findViewById(R.id.event_type_card);
            event_place = (TextView) view.findViewById(R.id.event_place_card);
            event_date_time = (TextView) view.findViewById(R.id.event_date_time_card);
            event_image = (ImageView) view.findViewById(R.id.event_image_card);
        }
    }

}