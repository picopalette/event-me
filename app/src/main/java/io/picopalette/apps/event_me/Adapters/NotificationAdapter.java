package io.picopalette.apps.event_me.Adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.picopalette.apps.event_me.Models.Event;


public class NotificationAdapter  extends EventsAdapter {
    public NotificationAdapter(Context context, List<Event> events) {
        super(context, events);
    }
}
