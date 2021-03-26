package com.margsapp.messenger;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Adapter.MessageAdapter;
import com.margsapp.messenger.Fragments.APIService;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.Notifications.Client;
import com.margsapp.messenger.Notifications.Data;
import com.margsapp.messenger.Notifications.MyResponse;
import com.margsapp.messenger.Notifications.Sender;
import com.margsapp.messenger.Notifications.Token;
import com.margsapp.messenger.reply.ISwipeControllerActions;
import com.margsapp.messenger.reply.SwipeController;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MESSAGE ACTIVITY" ;

    private static final String LIMIT = "0";
    CircleImageView profileImage;
    TextView username, statusText, reply_txt;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;


    ImageButton btnSend, btn_cancel_reply;
    EditText text_send;

    AppCompatButton btn_accept, btn_reject, btn_block;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    APIService apiService;

    String userid,ReplyId, Sendername,ReplyName,imageUrl,blocked;

    ConstraintLayout reply, editor;
    RelativeLayout warning;

    private InterstitialAd mInterstitialAd;

    boolean reply_ = false;

    boolean notify = false;
    ShortcutManager shortcutManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
             shortcutManager = getSystemService(ShortcutManager.class);
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/9926110222", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MessageActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });




        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        statusText = findViewById(R.id.status);

        reply = findViewById(R.id.reply_layout);
        editor = findViewById(R.id.editor_layout);
        warning = findViewById(R.id.warningcard);

        btn_accept = findViewById(R.id.btn_accept);
        btn_reject = findViewById(R.id.btn_reject);
        btn_block = findViewById(R.id.btn_block);

        btn_cancel_reply = findViewById(R.id.cancelButton);



        btnSend = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        reply_txt = findViewById(R.id.txtQuotedMsg);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



       

        SwipeController swipeController = new SwipeController(this, new ISwipeControllerActions() {
            @Override
            public void onSwipePerformed(int position) {
                onReply(mchat.get(position));
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btn_cancel_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideReplyLayout();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, Dp_viewActivity.class);
                intent.putExtra("data", imageUrl);
                startActivity(intent);
            }
        });





        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSend.setVisibility(View.VISIBLE);
                checkText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(firebaseUser.getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                assert chatlist != null;
                blocked = chatlist.getFriends();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(v -> {

            notify = true;
            String isseen = "false";
            String msg = text_send.getText().toString();
            if(!msg.equals(""))
            {
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                String timestamp = simpleDateFormat.format(calendar.getTime());


                String Reply = reply_txt.getText().toString();

                String Sender_name = Sendername;
                if(blocked.equals("blocked")){
                    String receiver = "blocked";
                    if(reply_){
                        ReplyMessage(firebaseUser.getUid(),receiver, msg, timestamp, isseen, Reply, ReplyId, Sendername,ReplyName);
                    }
                    if(!reply_){
                        sendMessage(firebaseUser.getUid(),receiver,msg, timestamp,isseen, Sender_name);
                    }
                }else {
                    if(reply_){
                        ReplyMessage(firebaseUser.getUid(),userid, msg, timestamp, isseen, Reply, ReplyId, Sendername,ReplyName);
                    }
                    if(!reply_){
                        sendMessage(firebaseUser.getUid(),userid,msg, timestamp,isseen, Sender_name);
                    }
                }



            }
            else {
                Toast.makeText(MessageActivity.this, "You cant send empty message Error code 0x08040101", Toast.LENGTH_SHORT).show();
            }

            text_send.setText("");

            hideReplyLayout();

        });

        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               Chatlist chatlist = snapshot.getValue(Chatlist.class);
                if(snapshot.exists()){
                    assert chatlist != null;
                    if(chatlist.getFriends().equals("Requested")) {
                        editor.setVisibility(View.GONE);
                        warning.setVisibility(View.VISIBLE);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(firebaseUser.getUid())
                        .child(userid);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("friends", "Messaged");
                databaseReference.getRef().updateChildren(hashMap);
                warning.setVisibility(View.GONE);
                editor.setVisibility(View.VISIBLE);
                Toast.makeText(MessageActivity.this, "Request Accepted!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(firebaseUser.getUid())
                        .child(userid);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("friends", "Rejected");
                databaseReference.getRef().updateChildren(hashMap);
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(MessageActivity.this, "Request Rejected!", Toast.LENGTH_SHORT).show();

            }
        });

        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                        .child(firebaseUser.getUid())
                        .child(userid);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("friends", "Blocked");
                databaseReference.getRef().updateChildren(hashMap);
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                Toast.makeText(MessageActivity.this, "User Blocked!", Toast.LENGTH_SHORT).show();
            }
        });




        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Sendername = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                statusText.setText(user.getStatus());
                imageUrl = user.getImageUrl();

                if(imageUrl.equals("default"))
                {
                    profileImage.setImageResource(R.drawable.user);

                }
                else {
                    Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
                }

                readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(userid);



    }

    private void checkText() {
        if(text_send.getText().toString().equals("")){
            btnSend.setVisibility(View.INVISIBLE);

        }


    }


    private void onReply(Chat getChat) {

        reply_txt.setText(getChat.getMessage());
        ReplyId = getChat.getSender();
        ReplyName = getChat.getSendername();
        reply.setVisibility(View.VISIBLE);
        text_send.isFocused();
        reply_ = true;

    }

    private void hideReplyLayout() {

        reply.setVisibility(View.GONE);
        reply_ = false;

    }


    private void seenMessage(String userid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", "true");
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ReplyMessage(String sender, String receiver, String message, String timestamp, String isseen,String ReplyMessage,String ReplyTo,String sendername,String replyname){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", isseen);
        hashMap.put("timestamp", timestamp);
        hashMap.put("reply", "true");
        hashMap.put("replytext", ReplyMessage);
        hashMap.put("replyto",ReplyTo);
        hashMap.put("sendername", sendername);
        hashMap.put("replyname", replyname);

        reference.child("Chats").push().setValue(hashMap);





        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiver);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatref.child("id").setValue(receiver);
                    chatref.child("friends").setValue("Messaged");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(firebaseUser.getUid());
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                    chatRefReceiver.child("friends").setValue("Requested");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final String msg = message;



        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    assert user != null;
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void sendMessage(String sender, String receiver, String message,String timestamp,String isseen,String sendername)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", isseen);
        hashMap.put("timestamp", timestamp);
        hashMap.put("reply", "false");
        hashMap.put("sendername", sendername);

        reference.child("Chats").push().setValue(hashMap);





        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiver);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatref.child("id").setValue(receiver);
                    chatref.child("friends").setValue("Messaged");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(firebaseUser.getUid());
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                if(!snapshot.exists()){
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                    chatRefReceiver.child("friends").setValue("Requested");

                }else if(snapshot.exists()){
                    assert chatlist != null;
                    if(chatlist.getFriends().equals("Rejected")){
                        chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                        chatRefReceiver.child("friends").setValue("Requested");
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        final String msg = message;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    assert user != null;
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    private void sendNotification(String receiver, String username, String message){

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(firebaseUser.getUid());

        chatref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                assert chatlist != null;
                if(snapshot.exists()){
                    if(!chatlist.getFriends().equals("Blocked")) {
                        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                        Query query = tokens.orderByKey().equalTo(receiver);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Token token = snapshot1.getValue(Token.class);
                                    Data data = new Data(firebaseUser.getUid(), R.drawable.ic_notification, username + ":" + message, "New Message",
                                            userid);
                                    assert token != null;
                                    Sender sender = new Sender(data, token.getToken());
                                    apiService.sendNotification(sender)
                                            .enqueue(new Callback<MyResponse>() {
                                                @Override
                                                public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                                                    if (response.code() == 200) {
                                                        assert response.body() != null;
                                                        if (response.body().success != 1) {
                                                            Toast.makeText(MessageActivity.this, "Failed! Error code 0x08060101", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<MyResponse> call, @NotNull Throwable t) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readMessage(String myid, String userid, String imageUrl){
        mchat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myid)&& chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid)&&chat.getSender().equals(myid)) {
                        mchat.add(chat);

                    }



                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageUrl);
                    recyclerView.setAdapter(messageAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_chat, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.block_user:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Are you sure you want to block this user?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Block();
                    }
                });
                dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dont do anything
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                break;

            case R.id.create_shortcut:
                Shortcuts(userid,username.getText().toString().trim(),profileImage.getDrawable());


        }

        return false;
    }



    private void Block(){

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatref.child("friends").setValue("Blocked");
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Toast.makeText(MessageActivity.this, "User Blocked", Toast.LENGTH_SHORT).show();


    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        databaseReference.updateChildren(hashMap);

    }





    private void Shortcuts(String userid, String username, Drawable imageUrl){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Bitmap bitmap = ((BitmapDrawable)imageUrl).getBitmap();
            if(shortcutManager.isRequestPinShortcutSupported()){
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(this, username)
                                .setShortLabel(username)
                                .setLongLabel(username)
                                .setIcon(Icon.createWithBitmap(bitmap))
                                .setIntents(
                                        new Intent[]{
                                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("userid",userid),
                                        })
                                .build();


                Intent pinnedShortcutCallbackIntent =
                        shortcutManager.createShortcutResultIntent(pinShortcutInfo);

                PendingIntent successCallback = PendingIntent.getBroadcast(this, /* request code */ 1,
                        pinnedShortcutCallbackIntent, /* flags */ 0);

                shortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }else {
                Toast.makeText(this,"Your current Android Version dosent support Shortcuts.",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"Your current Android Version dosent support Shortcuts.",Toast.LENGTH_SHORT).show();
        }
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {

            ShortcutInfo dynamicShortcut = new ShortcutInfo.Builder(this, username)
                    .setShortLabel(username)
                    .setLongLabel(username)
                    .setIcon(Icon.createWithBitmap(bitmap))
                    .setIntents(
                            new Intent[]{
                                    new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("userid",userid),
                            })
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut, dynamicShortcut));

        }else {
            Toast.makeText(this,"Your current Android Version dosent support Shortcuts.",Toast.LENGTH_SHORT).show();
        }

         */
    }




    public void onBackPressed(){
        finish();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MessageActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        status("online");


        currentUser(userid);
    }


    protected void onPause()
    {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }


}