package com.margsapp.messenger.groupclass;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.margsapp.messenger.Main.MainActivity;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class create_groupActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    CircleImageView group_image;
    AppCompatButton Next;

    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference reference, databaseReference;
    AppCompatEditText group_name;

    public String username;

    boolean image;

    String image_url;

    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(create_groupActivity.this, MainActivity.class)));

        group_image = findViewById(R.id.group_img);
        group_name = findViewById(R.id.groupname);
        Next = findViewById(R.id.next);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("GroupImages/"+ Objects.requireNonNull(group_name.getText()).toString());


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadingBar = new ProgressDialog(this);

        group_image.setOnClickListener(v -> openImage());



        Next.setOnClickListener(v -> {
            String txt_groupname = Objects.requireNonNull(group_name.getText()).toString();

            String image_;
            if(image){
                 image_ = image_url;
            }else {
                 image_ = "group_default";
            }

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
            String timestamp = simpleDateFormat.format(calendar.getTime());
            reference = FirebaseDatabase.getInstance().getReference("Group");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Group group = snapshot1.getValue(Group.class);
                            assert group != null;
                            if (txt_groupname.equals(group.getGroupname())) {
                                Toast.makeText(create_groupActivity.this, "Group Name already exists. Error code 0x08090101", Toast.LENGTH_SHORT).show();
                            }else if(!txt_groupname.equals(group.getGroupname())){
                                creategroup(txt_groupname, image_, timestamp, username);
                            }

                        }
                    }else {
                        creategroup(txt_groupname, image_, timestamp, username);
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });


    }

    private void creategroup(String txt_groupname, String image_, String timestamp, String username) {

        String groupID = randomString(12);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(firebaseUser.getUid()).child(groupID);
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("groupid", groupID);
        hashMap2.put("groupname", txt_groupname);
        hashMap2.put("admin","true");
        databaseReference1.setValue(hashMap2);




        reference = FirebaseDatabase.getInstance().getReference("Group").child(groupID);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupid",groupID);
        hashMap.put("groupname", txt_groupname);
        hashMap.put("imageUrl", image_);
        hashMap.put("createdon",timestamp);
        reference.setValue(hashMap).addOnCompleteListener(task -> {

            databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupID).child("members").child(firebaseUser.getUid());
            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("id", firebaseUser.getUid());
            hashMap1.put("user_name",username);
            hashMap1.put("admin","true");
            databaseReference.setValue(hashMap1);

            Intent intent = new Intent(create_groupActivity.this, AddParticipants.class);
            intent.putExtra("GroupID", groupID);
            startActivity(intent);
        });
    }

    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
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
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we update your group picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(resultUri));


                StorageTask uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(create_groupActivity.this, "Image has been stored in our servers", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            image_url = downloadUri.toString();
                            image = true;

                            Glide.with((getApplicationContext())).load(image_url).into(group_image);

                            loadingBar.dismiss();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = create_groupActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(create_groupActivity.this, MainActivity.class));
    }
}