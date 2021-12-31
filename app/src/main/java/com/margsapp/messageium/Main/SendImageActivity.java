package com.margsapp.messageium.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messageium.R;
import com.margsapp.messageium.utils.AES;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;


public class SendImageActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {


    ImageView imageView;

    ImageView cancel, cropopen,cropclose,crop_rotate, edit_open,edit_close;

    EditText text;

    ImageButton btn_send;

    CropImageView cropImageView;

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;

    Uri imageUri;

    Intent intent;

    RelativeLayout messagebox,crop_options;

    TextView done;

    StorageReference storageReference;

    String userid;

    private ProgressDialog loadingBar;

    DatabaseReference reference;

    private AES aes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);


        aes = new AES(this);

        intent = getIntent();


        imageUri = Uri.parse(intent.getStringExtra("imageUri"));

        userid = intent.getStringExtra("userid");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        storageReference = FirebaseStorage.getInstance().getReference("Chats").child(firebaseUser.getUid()).child(userid);


        btn_send = findViewById(R.id.btn_send);

        text = findViewById(R.id.text_send);


        imageView = findViewById(R.id.imageview);

        imageView.setImageURI(imageUri);


        cancel = findViewById(R.id.cancel);

        cropopen = findViewById(R.id.cropopen);
        cropclose = findViewById(R.id.cropclose);
        cropImageView = findViewById(R.id.cropImageView);

        edit_open = findViewById(R.id.edit_open);
        edit_close = findViewById(R.id.edit_close);
        photoEditorView = findViewById(R.id.photoEditorView);

        messagebox = findViewById(R.id.message_box);
        crop_options = findViewById(R.id.crop_options);
        crop_rotate = findViewById(R.id.crop_rotate);
        done = findViewById(R.id.done);

        loadingBar = new ProgressDialog(this);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        cropopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.setVisibility(View.VISIBLE);
                photoEditorView.setVisibility(View.GONE);
                messagebox.setVisibility(View.GONE);
                cropopen.setVisibility(View.GONE);
                cropclose.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                crop_options.setVisibility(View.VISIBLE);

                cropImageView.setImageUriAsync(imageUri);

                cropImage();


            }
        });

        cropclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImageView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                photoEditorView.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                cropclose.setVisibility(View.GONE);
                cropopen.setVisibility(View.VISIBLE);
                crop_options.setVisibility(View.GONE);

                imageView.setImageURI(imageUri);

            }
        });

        crop_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(-90);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cropImage();
                cropImageView.getCroppedImageAsync();
            }
        });




        edit_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditorView.getSource().setImageURI(imageUri);
                photoEditorView.setVisibility(View.VISIBLE);
                messagebox.setVisibility(View.GONE);
                cropImageView.setVisibility(View.GONE);
                edit_open.setVisibility(View.GONE);
                edit_close.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                crop_options.setVisibility(View.GONE);

                setupEditorView();

            }
        });

        edit_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEdit();

                photoEditorView.setVisibility(View.GONE);
                edit_close.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                edit_open.setVisibility(View.VISIBLE);
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Image");
                loadingBar.setMessage("Please wait, while we send picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                String message = text.getText().toString();



                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));



                StorageTask uploadTask = filepath.putFile(imageUri);
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

                            String sender = firebaseUser.getUid();
                            String receiver = userid;

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();

                            reference = FirebaseDatabase.getInstance().getReference("Chats");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", sender);
                            hashMap.put("receiver", receiver);
                            hashMap.put("message","\uD83D\uDDBC Image");
                            hashMap.put("reply","image");
                            hashMap.put("type","image");
                            hashMap.put("imageurl", mUri);
                            hashMap.put("isseen", "false");
                            hashMap.put("timestamp", timestamp);
                            reference.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(!message.equals("")){

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                        String sendername = intent.getStringExtra("sendername");
                                        String sender = firebaseUser.getUid();
                                        String receiver = userid;

                                        String encryptedmessage = aes.Encrypt(message, SendImageActivity.this);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("sender", sender);
                                        hashMap.put("receiver", receiver);
                                        hashMap.put("message", encryptedmessage);
                                        hashMap.put("isseen", "false");
                                        hashMap.put("timestamp", timestamp);
                                        hashMap.put("reply", "false");
                                        hashMap.put("sendername", sendername);

                                        reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                finish();
                                                loadingBar.dismiss();
                                            }
                                        });

                                    }else {
                                        finish();
                                        loadingBar.dismiss();
                                    }
                                }
                            });



                        }
                        else {
                            Toast.makeText(SendImageActivity.this, "Image was not able to send. Error Code", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });



                }
        });


    }


    private void setupEditorView() {
        Typeface mTextRobotoTf = ResourcesCompat.getFont(SendImageActivity.this, R.font.nunito);

        //loading font from assest
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
    }

    private void saveEdit() {

        photoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                imageView.setImageBitmap(saveBitmap);

                Log.d("imageUri", String.valueOf(imageUri));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = SendImageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void cropImage() {

        cropImageView.setOnSetImageUriCompleteListener(this);
        cropImageView.setOnCropImageCompleteListener(this);

    }


    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getApplicationContext(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getApplicationContext(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if(result.getError() == null){


            imageUri = result.getUri();


            imageView.setImageURI(imageUri);
            Log.d("imageUri", String.valueOf(imageUri));

            cropImageView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            photoEditorView.setVisibility(View.GONE);
            messagebox.setVisibility(View.VISIBLE);
            cropclose.setVisibility(View.GONE);
            cropopen.setVisibility(View.VISIBLE);
            crop_options.setVisibility(View.GONE);



        }

        else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    getApplicationContext(),
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }



}