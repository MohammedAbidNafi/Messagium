package com.margsapp.messageium.Settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.margsapp.messageium.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class setWallpaper extends AppCompatActivity {

    private static final String WALLPAPER = "WALLPAPER";
    RelativeLayout change_wallpaper;

    Dialog dialog;
    private static final int RC_PHOTO_PICKER =  105;

    ImageView preview_wallpaper;

    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);

        change_wallpaper = findViewById(R.id.change_wallpaper);

        preview_wallpaper = findViewById(R.id.preview_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.ChooseWallpaper));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
        String wallpaper = sharedPreferences.getString(WALLPAPER, "");

        switch(wallpaper){


            case "null":
                preview_wallpaper.setBackgroundColor(getResources().getColor(R.color.background));
                Log.d("imageBackground", "null");
                break;

            case "dark1":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark1));
                Log.d("imageBackground", "dark1");
                break;

            case "dark2":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark2));
                Log.d("imageBackground", "dark2");
                break;

            case "dark3":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark3));
                Log.d("imageBackground", "dark3");
                break;

            case "light1":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light1));
                Log.d("imageBackground", "light1");
                break;

            case "light2":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light2));
                Log.d("imageBackground", "light2");
                break;

            case "light3":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light3));
                Log.d("imageBackground", "light3");
                break;
        }

        dialog = new Dialog(this);
        SlidrInterface slidrInterface = Slidr.attach(this);

        change_wallpaper.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    change_wallpaper.setBackground(getResources().getDrawable(R.drawable.top_round_btn_onclick));

                }else {
                    change_wallpaper.setBackground(getResources().getDrawable(R.drawable.top_round_btn));

                }
                return false;
            }
        });


        change_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(setWallpaper.this, Wallpaper.class));
            }
        });


    }

    public void onMediaSelect(){

        AppCompatButton gallery,camera;

        CardView cancel;

        dialog.setContentView(R.layout.add_image_pop_up);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.setCancelable(false);

        gallery = dialog.findViewById(R.id.gallery);

        camera = dialog.findViewById(R.id.camera);

        cancel = dialog.findViewById(R.id.cancel);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                        RC_PHOTO_PICKER);
                dialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Camera!",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

        int x_ratio = metrics.widthPixels;
        int y_ratio = metrics.heightPixels;

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(x_ratio, y_ratio)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                assert result != null;
                Uri selectedImage = result.getUri();

                try {

                    Bitmap bitmap = getBitmapFromUri(selectedImage);
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    if(bitmap == null){
                        Log.d("Null", "Bitmap is null");
                    }else {
                        createDirectoryAndSaveFile(bitmap);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private Bitmap getBitmapFromUri(Uri selectedImage) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getApplicationContext().getContentResolver().openFileDescriptor(selectedImage, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave) {



            File dir = new File(Environment.getExternalStorageDirectory(),"MessengerByMargs");

            boolean isDirectoryCreated= dir.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated= dir.mkdirs();
            }

            File file = new File(dir,System.currentTimeMillis()+".jpg");
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            imageToSave.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

            Toast.makeText(getApplicationContext(),"Image Saved!", Toast.LENGTH_SHORT).show();
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



    }


    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
        String wallpaper = sharedPreferences.getString(WALLPAPER, "");

        switch(wallpaper){


            case "null":
                preview_wallpaper.setBackgroundColor(getResources().getColor(R.color.background));
                Log.d("imageBackground", "null");
                break;

            case "dark1":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark1));
                Log.d("imageBackground", "dark1");
                break;

            case "dark2":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark2));
                Log.d("imageBackground", "dark2");
                break;

            case "dark3":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.dark3));
                Log.d("imageBackground", "dark3");
                break;

            case "light1":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light1));
                Log.d("imageBackground", "light1");
                break;

            case "light2":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light2));
                Log.d("imageBackground", "light2");
                break;

            case "light3":
                preview_wallpaper.setBackground(getResources().getDrawable(R.drawable.light3));
                Log.d("imageBackground", "light3");
                break;
        }
    }



}