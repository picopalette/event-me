package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.R;

/**
 * Created by ramkumar on 12/07/17.
 */

public class PeopleSearchCardViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView imageView;
    public TextView nameView;
    public TextView emailView;
    public CardView addButton;
    public AppCompatImageView statusView;

    public PeopleSearchCardViewHolder(View itemView) {
        super(itemView);
        imageView = (CircleImageView) itemView.findViewById(R.id.card_people_search_imageView);
        nameView = (TextView) itemView.findViewById(R.id.card_people_search_nameView);
        emailView = (TextView) itemView.findViewById(R.id.card_people_search_emailView);
        addButton = (CardView) itemView.findViewById(R.id.card_people_search_addBtn);
        statusView = (AppCompatImageView) itemView.findViewById(R.id.people_add_status);
    }
}
