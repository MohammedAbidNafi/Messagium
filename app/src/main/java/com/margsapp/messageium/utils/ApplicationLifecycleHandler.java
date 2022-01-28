package com.margsapp.messageium.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    DatabaseReference reference;

    FirebaseUser firebaseUser;
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        status("online");

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        status("online");


    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        status("online");

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        status("offline");

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        status("offline");

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        status("offline");

    }

    @Override
    public void onTrimMemory(int i) {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {

    }

    @Override
    public void onLowMemory() {

    }


    private void status(String status){
    /*    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        reference.updateChildren(hashMap);

     */
    }

}
