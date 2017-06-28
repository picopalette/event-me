package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;

import io.picopalette.apps.event_me.R;

/**
 * Created by ramkumar on 28/06/17.
 */

public class PersonalListViewViewHolder extends RecyclerView.ViewHolder {

    public CheckedTextView listItemCheckedTextView;

    public PersonalListViewViewHolder(View itemView) {
        super(itemView);
        listItemCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.list_item_display_CheckedTextView);
    }
}
