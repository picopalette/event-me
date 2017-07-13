package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.picopalette.apps.event_me.R;

/**
 * Created by ramkumar on 13/07/17.
 */

public class TeamsListCardViewHolder extends RecyclerView.ViewHolder {

    public TextView teamNameView;

    public TeamsListCardViewHolder(View itemView) {
        super(itemView);
        teamNameView = (TextView) itemView.findViewById(R.id.card_teams_list_name_textView);
    }
}
