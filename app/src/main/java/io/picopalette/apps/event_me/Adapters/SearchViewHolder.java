package io.picopalette.apps.event_me.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.picopalette.apps.event_me.R;

/**
 * Created by holmesvinn on 13/7/17.
 */

public class SearchViewHolder extends RecyclerView.ViewHolder {
    public TextView search;
    public CircleImageView imageView;

    public SearchViewHolder(View itemView) {
        super( itemView );
        search = (TextView) itemView.findViewById( R.id.searchimageText );
        imageView = (CircleImageView) itemView.findViewById( R.id.searchImage );
    }
}
