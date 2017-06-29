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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.Utilities;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    public List<Event> events;
    private int keep = 0;
    public View itemView;
    private Context context;
    private RecyclerView recyclerView;
    private SimpleDateFormat sdf;
    private String currentTime;
    private RecyclerViewReadyCallback recyclerViewReadyCallback;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public NotificationAdapter(Context context, List<Event> events,RecyclerView recyclerView)  {
        this.events = events;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_activity_card, parent, false);
        return new NotifViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotifViewHolder holder, final int position) {
        Log.d("TESTI", "inside bindviewholder");
        final Event homeEvent = events.get(position);

        holder.NotificationText.setText("You have been Invited to "+homeEvent.getName());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(Constants.users)
                    .child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .child(Constants.events).child(homeEvent.getId()).setValue(Constants.UserStatus.GOING);
                events.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,events.size());
                holder.itemView.setVisibility(View.GONE);

            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDisplayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("event", homeEvent);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != events ? events.size() : 0);
    }


    public class NotifViewHolder extends RecyclerView.ViewHolder {
        public TextView NotificationText, accept, decline;
        public NotifViewHolder(View itemView) {
            super(itemView);
            NotificationText = (TextView) itemView.findViewById(R.id.notfication_tv);
            accept = (TextView) itemView.findViewById(R.id.accept_tv);
            decline = (TextView) itemView.findViewById(R.id.decline_tv);
        }
    }
}