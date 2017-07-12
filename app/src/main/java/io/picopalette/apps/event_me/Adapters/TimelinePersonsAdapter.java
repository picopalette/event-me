package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;

/**
 * Created by Aswin Sundar on 12-07-2017.
 */

public class TimelinePersonsAdapter extends RecyclerView.Adapter<TimelinePersonsAdapter.PersonsViewHolder> {

    private Context context;
    private ArrayList<String> participantEmails;

    public TimelinePersonsAdapter (Context context,ArrayList<String> participantEmails){
        this.context = context;
        this.participantEmails = participantEmails;
    }


    @Override
    public PersonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_timeline_persons, parent, false);
        return new PersonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonsViewHolder holder, int position) {

        String emailKey = participantEmails.get(position);
        Log.d("Email", emailKey);
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.nameView.setText(user.getDisplayName());
                Glide.with(context).load(user.getDpUrl()).into(holder.imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return participantEmails.size();
    }

    public class PersonsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView nameView;

        public PersonsViewHolder(View itemView) {
            super(itemView);

            imageView = (CircleImageView) itemView.findViewById(R.id.timelineProfilePic);
            nameView = (TextView) itemView.findViewById(R.id.timelineProfileName);
        }
    }
}
