package io.picopalette.apps.event_me.Fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import io.picopalette.apps.event_me.R;

public class ProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener{



    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    private Bundle userBundle;
    private Uri coverUri = Uri.parse("");
    private Uri imageUri = Uri.parse("http://i.imgur.com/VIlcLfg.jpg");

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private TextView profileName;
    private TextView profileEmail;
    private SimpleDraweeView avatar;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(this.getContext());
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        userBundle = getArguments();
        appbar = (AppBarLayout) v.findViewById( R.id.appbar );
        collapsing = (CollapsingToolbarLayout) v.findViewById( R.id.collapsing );
        coverImage = (ImageView) v.findViewById( R.id.imageview_placeholder );
        framelayoutTitle = (FrameLayout) v.findViewById( R.id.framelayout_title );
        linearlayoutTitle = (LinearLayout) v.findViewById( R.id.linearlayout_title );
        toolbar = (Toolbar) v.findViewById( R.id.toolbar );
        textviewTitle = (TextView) v.findViewById( R.id.textview_title );
        profileName = (TextView) v.findViewById(R.id.profile_view_name);
        profileEmail = (TextView) v.findViewById(R.id.profile_view_email);
        avatar = (SimpleDraweeView) v.findViewById(R.id.avatar);
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
        //set avatar and cover
        imageUri = Uri.parse(userBundle.getString("dpurl"));
        avatar.setImageURI(imageUri);
        coverImage.setImageURI(coverUri);
        profileName.setText(userBundle.getString("name"));
        profileEmail.setText(userBundle.getString("email"));
        return v;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


}