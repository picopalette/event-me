package io.picopalette.apps.event_me.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mzelzoghbi.zgallery.activities.ZGalleryActivity;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.picopalette.apps.event_me.R;
import io.picopalette.apps.event_me.Utils.Constants;
import io.picopalette.apps.event_me.Utils.EventGallery;

/**
 * Created by Rajesh-Rk on 08-07-2017.
 */

public class EventGalleryActivity extends ZGalleryActivity {

    private int REQUEST_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int uploadTaskCount = 0;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.event_gallery_upload_new) {
            ImagePicker.create(EventGalleryActivity.this)
                            .folderMode(true)
                            .folderTitle(getString(R.string.my_images))
                            .imageTitle(getString(R.string.select))
                            .showCamera(true)
                            .multi()
                            .imageDirectory("Images/ *jpg")
                            .start(REQUEST_CODE_PICKER);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String eventId = getIntent().getStringExtra("eventId");
        dbRef = FirebaseDatabase.getInstance().getReference().child(Constants.gallery).child(eventId);
        Log.d("image_pick_eventId", eventId);
        progressDialog = new ProgressDialog(EventGalleryActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Taking your memories into space");
        progressDialog.setCancelable(false);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images =  data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            Log.d("selected images", images.toString());
            progressDialog.show();
            for(Image image : images) {
                Log.d("selected image path", image.getPath());
                Uri file = Uri.fromFile(new File(image.getPath()));
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StorageReference eventGalleryRef = FirebaseStorage.getInstance().getReference().child("gallery/"+eventId+"/"+file.getLastPathSegment());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if(bitmap == null) {
                    progressDialog.dismiss();
                    return;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imageData = baos.toByteArray();
                UploadTask uploadTask = eventGalleryRef.putBytes(imageData);
                uploadTaskCount++;
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        uploadTaskCount--;
                        if(uploadTaskCount == 0 && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        pushUrlToDb(downloadUrl);
                        uploadTaskCount--;
                        if(uploadTaskCount == 0 && progressDialog.isShowing()) {
                            Toast.makeText(EventGalleryActivity.this, "All images uploaded", Toast.LENGTH_SHORT).show();
                            EventGalleryActivity.this.finish();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void pushUrlToDb(Uri downloadUrl) {
        String key = dbRef.push().getKey();
        dbRef.child(key).setValue(downloadUrl.toString());
    }

}
