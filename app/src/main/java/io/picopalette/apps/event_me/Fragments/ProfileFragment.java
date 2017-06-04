package io.picopalette.apps.event_me.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.picopalette.apps.event_me.MainActivity;
import io.picopalette.apps.event_me.R;
import jp.wasabeef.blurry.Blurry;

/**
 * Created by ramkumar on 02/06/17.
 */

public class ProfileFragment extends Fragment {

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        int imageresource = getResources().getIdentifier("@drawable/logo", "drawable", getActivity().getPackageName());
        ImageView imageView = (ImageView) v.findViewById(R.id.header_cover_image);

        Bitmap logo = BitmapFactory.decodeResource(getContext().getResources(), imageresource);
        Blurry.with(getContext()).from(logo).into(imageView);

        return v;
    }

}
