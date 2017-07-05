package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import io.picopalette.apps.event_me.R;

/**
 * Created by Aswin Sundar on 27-06-2017.
 */

public class PersonalListEditViewHolder extends RecyclerView.ViewHolder {

    public TextView listTextView;
    public ImageButton listDeleteButton;

    public PersonalListEditViewHolder(View itemView) {
        super(itemView);
        listTextView = (TextView) itemView.findViewById(R.id.list_textView);
        listDeleteButton = (ImageButton) itemView.findViewById(R.id.removeFAB);
    }
}
