package io.picopalette.apps.event_me.Models;

/**
 * Created by holmesvinn on 14/7/17.
 */

public class SearchModel {
    private final long mId;
    private final String mText;

    public SearchModel(long id, String text) {
        mId = id;
        mText = text;
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }
}
