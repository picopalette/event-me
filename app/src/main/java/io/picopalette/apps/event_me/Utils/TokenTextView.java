package io.picopalette.apps.event_me.Utils;

import android.content.Context;
import android.util.AttributeSet;
import io.picopalette.apps.event_me.R;

public class TokenTextView extends android.support.v7.widget.AppCompatTextView {

    public TokenTextView(Context context) {
        super(context);
    }
    public TokenTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.ic_clear_white_18dp : 0, 0);
    }
}