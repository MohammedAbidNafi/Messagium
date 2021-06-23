package com.margsapp.messenger.groupclass;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messenger.Fragments.GroupInfoFragment;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.R;
import com.margsapp.messenger.dp_view.group_dpActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class group_infoActivity extends AppCompatActivity {

    ImageView group_img;

    Intent intent;
    String groupid, groupname;

    String imageUrl;

    FloatingActionButton cameraButton;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int GALLERY_PICK = 1;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cameraButton = findViewById(R.id.camera);
        group_img = findViewById(R.id.group_img);

        intent = getIntent();
        groupid = intent.getStringExtra("groupid");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                assert group != null;
                imageUrl = group.getImageUrl();
                groupname = group.getGroupname();
                if(imageUrl.equals("group_default")){
                    group_img.setImageResource(R.drawable.groupicon);
                }else {
                    Glide.with(getApplicationContext()).load(group.getImageUrl()).into(group_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        toolbar.setNavigationOnClickListener(v -> {
            Intent openMainActivity = new Intent(group_infoActivity.this, group_messageActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            openMainActivity.putExtra("groupid",groupid);
            startActivityIfNeeded(openMainActivity, 0);
        });


        group_img.setOnClickListener(v -> {
            String data = imageUrl;

            Intent intent = new Intent(group_infoActivity.this, group_dpActivity.class);

            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(group_img, "imageTransition");


            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(group_infoActivity.this, pairs);
            intent.putExtra("data", data);
            intent.putExtra("groupid",groupid);
            intent.putExtra("groupname",groupname);
            startActivity(intent, options.toBundle());


        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("GroupImages/"+ groupid);
        loadingBar = new ProgressDialog(this);

        cameraButton.setOnClickListener(v -> openImage());


        final ViewPager viewPager = findViewById(R.id.viewPager);
        group_infoActivity.ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new GroupInfoFragment(), "GroupInfo");
        viewPager.setAdapter(viewPageAdapter);


    }

    public String getMyData() {
        return groupid;
    }

    static class ViewPageAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void openImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Group Image");
                loadingBar.setMessage("Please wait, while we update your picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(resultUri));

                StorageTask uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(group_infoActivity.this, "Image has been stored in our servers", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();


                            reference = FirebaseDatabase.getInstance().getReference("Group").child(groupid);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageUrl", mUri);
                            reference.updateChildren(map);

                            loadingBar.dismiss();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Picture updation has cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = group_infoActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @Override
    public void onBackPressed() {
        Intent openMainActivity = new Intent(group_infoActivity.this, group_messageActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        openMainActivity.putExtra("groupid",groupid);
        startActivityIfNeeded(openMainActivity, 0);
    }
}