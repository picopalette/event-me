package io.picopalette.apps.event_me.Utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mzelzoghbi.zgallery.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mzelzoghbi.zgallery.activities.ZGalleryActivity;
import com.mzelzoghbi.zgallery.entities.ZColor;
import java.util.ArrayList;
import io.picopalette.apps.event_me.Activities.EventGalleryActivity;

public class EventGallery  {
    private Context mActivity;
    private ArrayList<String> imagesURLs;
    private String eventId;
    private String title;
    private int spanCount = 2;
    private int toolbarColor = -1;
    private int imgPlaceHolderResId = -1;
    private ZColor color;
    private int selectedImgPosition;
    private ZColor backgroundColor;

    private EventGallery() {
    }

    public static EventGallery with(Context activity, String eventId) {
        return new EventGallery(activity, eventId);
    }

    private EventGallery(Context activity, String eventId) {
        this.eventId = eventId;
        this.mActivity = activity;
        this.imagesURLs = new ArrayList<>();
    }

    /**
     * Set z_toolbar title
     *
     * @param title
     * @return
     */
    public EventGallery setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Setting z_toolbar Color ResourceId
     *
     * @param colorResId
     * @return
     */
    public EventGallery setToolbarColorResId(int colorResId) {
        this.toolbarColor = colorResId;
        return this;
    }

    /**
     * Setting z_toolbar color
     *
     * @param color enum color may be black or white
     * @return
     */
    public EventGallery setToolbarTitleColor(ZColor color) {
        this.color = color;
        return this;
    }

    /**
     * Setting starting position
     *
     * @param selectedImgPosition
     * @return
     */
    public EventGallery setSelectedImgPosition(int selectedImgPosition) {
        this.selectedImgPosition = selectedImgPosition;
        return this;
    }

    public EventGallery setGalleryBackgroundColor(ZColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * Start the gallery activity with builder settings
     */
    public void show() {
        FirebaseDatabase.getInstance().getReference().child(io.picopalette.apps.event_me.Utils.Constants.gallery).child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot url : dataSnapshot.getChildren()) {
                    imagesURLs.add(url.getValue().toString());
                }
                Log.d("imageUrls", imagesURLs.toString());
                Intent gridActivity = new Intent(mActivity, EventGalleryActivity.class);
                gridActivity.putExtra(Constants.IntentPassingParams.IMAGES, imagesURLs);
                gridActivity.putExtra(Constants.IntentPassingParams.TITLE, title);
                gridActivity.putExtra(Constants.IntentPassingParams.TOOLBAR_COLOR_ID, toolbarColor);
                gridActivity.putExtra(Constants.IntentPassingParams.TOOLBAR_TITLE_COLOR, color);
                gridActivity.putExtra(Constants.IntentPassingParams.SELECTED_IMG_POS, selectedImgPosition);
                gridActivity.putExtra(Constants.IntentPassingParams.BG_COLOR, backgroundColor);
                gridActivity.putExtra("eventId", eventId);
                gridActivity.putExtra("title", title);
                if(imagesURLs.isEmpty()) {
                    Toast.makeText(mActivity, "Gallery is Empty, Add some Memories", Toast.LENGTH_LONG).show();
                }
                mActivity.startActivity(gridActivity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
