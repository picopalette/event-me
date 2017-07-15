package io.picopalette.apps.event_me.Adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Activities.EventDisplayActivity;
import io.picopalette.apps.event_me.Activities.NotificationsActivity;
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
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(homeEvent.getOwner()).child("displayName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                holder.NotificationText.setText("You have been Invited to "+homeEvent.getName()+ " by "+name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.accept.setText("ACCEPT");
        holder.decline.setText("DECLINE");
        holder.event_title.setText(homeEvent.getName());
        holder.event_date.setText(homeEvent.getDateAndTime().getFormattedDate());
        holder.event_time.setText(homeEvent.getDateAndTime().getFormattedTime());
        holder.event_place.setText(homeEvent.getPlace().getName());
        holder.event_hashtag.setText(homeEvent.getType());
        holder.event_p_count.setText(String.valueOf(homeEvent.getParticipants().size()));
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

            }
        });
        Log.d("sizes", String.valueOf(events.size()));
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(Constants.users)
                    .child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .child(Constants.events).child(homeEvent.getId()).setValue(Constants.UserStatus.GOING);


                events.remove(holder.getAdapterPosition());
                Log.d("sizes2", String.valueOf(events.size()));
                notifyItemRemoved(holder.getAdapterPosition());
                Log.d("sizes3", String.valueOf(events.size()));
                notifyItemRangeRemoved(holder.getAdapterPosition(),events.size());
                Log.d("sizes4", String.valueOf(events.size()));
                events.clear();
                notifyDataSetChanged();


            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
//                        } else {
                builder = new AlertDialog.Builder(context);
//                        }
                builder.setTitle("Delete Event Request")
                        .setMessage("You can't undo the action")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                FirebaseDatabase.getInstance().getReference().child(Constants.users)
                                        .child(Utilities.encodeEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                                        .child(Constants.events).child(homeEvent.getId()).removeValue();
                                events.remove(holder.getAdapterPosition());
                                Log.d("sizes2", String.valueOf(events.size()));
                                notifyItemRemoved(holder.getAdapterPosition());
                                Log.d("sizes3", String.valueOf(events.size()));
                                notifyItemRangeRemoved(holder.getAdapterPosition(),events.size());
                                Log.d("sizes4", String.valueOf(events.size()));
                                events.clear();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        holder.evevntView.setOnClickListener(new View.OnClickListener() {
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
        return events.size();
    }


    public class NotifViewHolder extends RecyclerView.ViewHolder {
        public TextView NotificationText, accept, decline;
        public View evevntView;
        TextView event_title, event_place, event_date, event_time, event_p_count, event_hashtag;
        CircleImageView event_image;
        public NotifViewHolder(View itemView) {
            super(itemView);
            NotificationText = (TextView) itemView.findViewById(R.id.notfication_tv);
            accept = (TextView) itemView.findViewById(R.id.accept_tv);
            decline = (TextView) itemView.findViewById(R.id.decline_tv);
            evevntView = itemView.findViewById(R.id.notification_event_card);
            event_title = (TextView) evevntView.findViewById(R.id.event_name_card);
            event_place = (TextView) evevntView.findViewById(R.id.event_place_card);
            event_date = (TextView) evevntView.findViewById(R.id.event_date_card);
            event_time = (TextView) evevntView.findViewById(R.id.event_time_card);
            event_image = (CircleImageView) evevntView.findViewById(R.id.event_image_card);
            event_hashtag = (TextView) evevntView.findViewById(R.id.event_card_hashtag);
            event_p_count = (TextView) evevntView.findViewById(R.id.event_card_personCount);
        }
    }
}