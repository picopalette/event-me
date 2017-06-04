package io.picopalette.apps.event_me.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import io.picopalette.apps.event_me.R;


import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PICKER = 69;
    private static Bitmap myBitmap;
    private ImageView header_image,profile_change,imageView;
    private CircleImageView profile_pic;
    private TextView full_name, email_id;
    private ProgressDialog progressDialog;
    private String img_url;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        progressDialog = new ProgressDialog(getContext());
        header_image = (ImageView) v.findViewById(R.id.header_cover_image);
        profile_change = (ImageView) v.findViewById(R.id.profile_changer);
        profile_pic = (CircleImageView) v.findViewById(R.id.profile_image);
        full_name = (TextView) v.findViewById(R.id.user_profile_name);
        email_id = (TextView) v.findViewById(R.id.user_profile_email);
        progressDialog.setMessage("Getting your content");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null)
        {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            assert photoUrl != null;
            img_url = photoUrl.toString();
            full_name.setText(name);
            email_id.setText(email);
            Glide.with(getContext()).load(photoUrl).into(profile_pic);
            Glide.with(getContext()).load(photoUrl).into(header_image);

        }
        progressDialog.dismiss();

        profile_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagestart();
            }
        });


        return v;
    }

    private void imagestart() {
        ImagePicker.create(getActivity())
                .folderMode(true)
                .folderTitle("My Images")
                .imageTitle("Select")
                .single()
                .showCamera(false)
                .imageDirectory("Images/*.jpg")
                .start(REQUEST_CODE_PICKER);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            StringBuilder sb = new StringBuilder();
            for (int i = 0, l = images.size(); i < l; i++) {
                sb.append(images.get(i).getPath()).append("\n");
            }
            String uri = sb.toString();
            profile_pic.setImageURI(Uri.parse(uri.trim()));
            header_image.setImageURI(Uri.parse(uri.trim()));

        }
    }


}
