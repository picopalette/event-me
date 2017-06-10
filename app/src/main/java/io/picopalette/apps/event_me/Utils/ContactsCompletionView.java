package io.picopalette.apps.event_me.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.tokenautocomplete.TokenCompleteTextView;
import io.picopalette.apps.event_me.Models.SimpleContact;
import io.picopalette.apps.event_me.R;

public class ContactsCompletionView extends TokenCompleteTextView<SimpleContact> {

    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(SimpleContact contact) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View tokenView = l.inflate(R.layout.item_autocomplete_contact, (ViewGroup) getParent(), false);
        TokenTextView textView = (TokenTextView) tokenView.findViewById(R.id.token_text);
        ImageView icon = (ImageView) tokenView.findViewById(R.id.icon);
        textView.setText(contact.getName());
        icon.setImageResource(contact.getDrawableId());
        return tokenView;
    }

    @Override
    protected SimpleContact defaultObject(String completionText) {
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new SimpleContact(R.mipmap.male, completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new SimpleContact(R.mipmap.male, completionText.substring(0, index), completionText);
        }
    }
}