package io.picopalette.apps.event_me.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.Models.User;
import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;

/**
 * Created by Rajesh-Rk on 06-07-2017.
 */

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder> {

    private Context context;
    private ArrayList<String> participantEmails;
    private  ArrayList<Constants.UserStatus> participantstatus;

    public ParticipantsAdapter(Context context, ArrayList<String> participantEmails, ArrayList<Constants.UserStatus> participantstatus) {
        this.context = context;
        this.participantEmails = participantEmails;
        this.participantstatus = participantstatus;
    }

    @Override
    public ParticipantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list_card, parent, false);
        return new ParticipantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ParticipantsViewHolder holder, int position) {
        String emailKey = participantEmails.get(position);
        final String userstat = String.valueOf(participantstatus.get(position));
        Log.d("userEmail", emailKey);
        FirebaseDatabase.getInstance().getReference().child(Constants.users).child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.nameView.setText(user.getDisplayName());
                holder.emailView.setText(user.getEmail()+"\n"+userstat);
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

    static public class ParticipantsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView nameView, emailView;
        public CardView card;

        public ParticipantsViewHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.p_list_image);
            nameView = (TextView) itemView.findViewById(R.id.p_list_name);
            emailView = (TextView) itemView.findViewById(R.id.p_list_email);
            card = (CardView) itemView.findViewById(R.id.p_list_card);
        }
    }
}
