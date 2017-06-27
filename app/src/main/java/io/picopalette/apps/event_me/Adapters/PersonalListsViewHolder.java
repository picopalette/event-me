package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.picopalette.apps.event_me.R;

/**
 * Created by Aswin Sundar on 27-06-2017.
 */

public class PersonalListsViewHolder extends RecyclerView.ViewHolder {

    public TextView personalListTitle;
    public LinearLayout personalListView, personalListEdit, personalListDelete, personalListActions;
    public CardView personalListCard;
    public PersonalListsViewHolder(View itemView) {
        super(itemView);
        personalListTitle = (TextView) itemView.findViewById(R.id.personal_list_title_TextView);
        personalListView = (LinearLayout) itemView.findViewById(R.id.personal_list_action_view);
        personalListEdit = (LinearLayout) itemView.findViewById(R.id.personal_list_action_edit);
        personalListDelete = (LinearLayout) itemView.findViewById(R.id.personal_list_action_delete);
        personalListActions = (LinearLayout) itemView.findViewById(R.id.personal_list_actions);
        personalListCard = (CardView) itemView.findViewById(R.id.personal_list_card);
    }
}
