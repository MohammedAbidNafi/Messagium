package com.margsapp.messenger.Notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.margsapp.messenger.Main.MainActivity;
import com.margsapp.messenger.Main.MessageActivity;
import com.margsapp.messenger.video_call.IncomingCallActivity;

import org.jetbrains.annotations.NotNull;



public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {


        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");
        String group = remoteMessage.getData().get("group");




        String VideoCall = remoteMessage.getData().get("VideoCall");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentuser = preferences.getString("currentuser", "none");


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(VideoCall != null){
            if(VideoCall.equals("true")){

                assert sented != null;
                if(sented.equals(firebaseUser.getUid())){
                    sendIncomingCall(remoteMessage);
                }
            }
        }



        assert group != null;
        if(group.equals("false")){
            if (firebaseUser != null) {
                assert sented != null;
                if (sented.equals(firebaseUser.getUid())) {

                    if (!currentuser.equals(user)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            sendOreoNotification(remoteMessage);
                        } else {
                            sendNotification(remoteMessage);
                        }
                    }


                }
            }
        }else {
            if (firebaseUser != null) {
                assert sented != null;
                if (sented.equals(firebaseUser.getUid())) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoGroupNotification(remoteMessage);
                    } else {
                        sendGroupNotification(remoteMessage);
                    }
                }
            }
        }

    }

    private void sendIncomingCall(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String imageUrl = remoteMessage.getData().get("imageurl");
        String username = remoteMessage.getData().get("username");

        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(getApplicationContext(),IncomingCallActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("username", username);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }



    private void sendGroupNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        /*
        Bundle bundle = new Bundle();
        bundle.putString("groupid", user);
        intent.putExtras(bundle);

         */
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;

        if(j>0){
            i=j;
        }

        notificationManager.notify(i, builder.build());
    }

    private void sendOreoGroupNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MainActivity.class);
        /*
        Bundle bundle = new Bundle();
        bundle.putString("groupid", user);
        intent.putExtras(bundle);

         */
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;

        if(j>0){
            i=j;
        }

        oreoNotification.getManager().notify(i, builder.build());
    }


    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user!=null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;

        if(j>0){
            i=j;
        }

        oreoNotification.getManager().notify(i, builder.build());


    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert icon != null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;

        if(j>0){
            i=j;
        }

        notificationManager.notify(i, builder.build());
    }
}


