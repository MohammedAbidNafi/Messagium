package com.margsapp.messageium.groupclass;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.factor.bouncy.BouncyRecyclerView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messageium.Adapter.GroupMessageAdapter;
import com.margsapp.messageium.Main.MainActivity;
import com.margsapp.messageium.Notifications.APIService;
import com.margsapp.messageium.Model.Group;
import com.margsapp.messageium.Model.GroupChat;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.Notifications.Client;
import com.margsapp.messageium.Notifications.Data;
import com.margsapp.messageium.Notifications.MyResponse;
import com.margsapp.messageium.Notifications.Sender;
import com.margsapp.messageium.Notifications.Token;
import com.margsapp.messageium.R;
import com.margsapp.messageium.reply.SwipeController;
import com.margsapp.messageium.utils.AES;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.margsapp.messageium.Settings.CustomiseActivity.THEME;

public class group_messageActivity extends AppCompatActivity {

    private static final String TAG = "GROUPMESSAGE ACTIVITY" ;
    CircleImageView group_image;
    TextView groupusername, part_names, reply_txt;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,partdatabaseReference;


    private AES aes;

    ImageButton btnSend, btn_cancel_reply;
    EditText text_send;

    AppCompatButton btn_accept, btn_reject, btn_block;

    GroupMessageAdapter groupMessageAdapter;

    List<GroupChat> mchat;
    List<String> mUsernames;

    BouncyRecyclerView recyclerView;

    Intent intent;

    APIService apiService;

    String groupid,groupname_,ReplyId,Replyname,username;

    String imageUrl;

    ConstraintLayout reply,group_info;
    RelativeLayout warning,editor;

    private SlidrInterface slidrInterface;

    private InterstitialAd mInterstitialAd;

    boolean reply_ = false;

    boolean notify = false;

    ShortcutManager shortcutManager;

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("theme", 0);
        String Theme = preferences.getString(THEME, "");
        if(Theme.equals("2")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(Theme.equals("1")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(Theme.equals("0")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        intent = getIntent();
        groupid = intent.getStringExtra("groupid");

        aes = new AES(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        group_info = findViewById(R.id.group_info);



        group_info.setOnTouchListener((v, event) -> {



            if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                group_info.setBackgroundColor(getResources().getColor(R.color.onToolClick));

            }else {
                group_info.setBackgroundColor(getResources().getColor(R.color.coral));

            }
            return false;
        });
        group_info.setOnClickListener(v -> {
            startActivity(new Intent(group_messageActivity.this, group_infoActivity.class).putExtra("groupid", groupid));
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
        }


        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(group_messageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            startActivity(intent);


        });




        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        group_image = findViewById(R.id.group_img);
        groupusername = findViewById(R.id.groupname);

        part_names = findViewById(R.id.participants_name);

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


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setFlingAnimationSize(0.3f);
        recyclerView.setOverscrollAnimationSize(0.3f);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        SwipeController swipeController = new SwipeController(this, position -> onReply(mchat.get(position)));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btn_cancel_reply.setOnClickListener(v -> hideReplyLayout());

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



        btnSend.setOnClickListener(v -> {

            notify = true;
            String msg = text_send.getText().toString();
            if(!msg.equals(""))
            {
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                String Reply = reply_txt.getText().toString();

                if(reply_){
                    ReplyMessage(firebaseUser.getUid(),username, msg, timestamp, Reply, ReplyId,Replyname);
                }
                if(!reply_){
                    sendMessage(firebaseUser.getUid(),username,msg, timestamp);
                }
            }
            else {
                Toast.makeText(group_messageActivity.this, "You cant send empty message Error code 0x08040101", Toast.LENGTH_SHORT).show();
            }

            text_send.setText("");

            hideReplyLayout();

        });


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
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


        databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                assert group != null;
                groupusername.setText(group.getGroupname());
                groupname_ = group.getGroupname();
                imageUrl = group.getImageUrl();
                //Participantsname
                if(imageUrl.equals("group_default"))
                {
                    group_image.setImageResource(R.drawable.groupicon);

                }
                else {
                    Glide.with(getApplicationContext()).load(group.getImageUrl()).into(group_image);
                }
                readMessage(groupid);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mUsernames = new ArrayList<>();
        partdatabaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");
        partdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                mUsernames.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Group group1 = snapshot1.getValue(Group.class);

                    assert group1 != null;
                    mUsernames.add(group1.getUser_name());

                }

                StringBuilder str = new StringBuilder("");

                // Traversing the ArrayList
                for (String eachstring : mUsernames) {

                    // Each element in ArrayList is appended
                    // followed by comma
                    str.append(eachstring).append(" , ");
                }

                // StringBuffer to String conversion
                String commaseparatedlist = str.toString();

                // By following condition you can remove the last
                // comma
                if (commaseparatedlist.length() > 0)
                    commaseparatedlist
                            = commaseparatedlist.substring(
                            0, commaseparatedlist.length() - 1);

                part_names.setText(commaseparatedlist);
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });

        mUsernames = new ArrayList<>();
        partdatabaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");
        partdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                mUsernames.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Group group1 = snapshot1.getValue(Group.class);

                    assert group1 != null;
                    mUsernames.add(group1.getUser_name());

                }

                StringBuilder str = new StringBuilder("");

                // Traversing the ArrayList
                for (String eachstring : mUsernames) {

                    // Each element in ArrayList is appended
                    // followed by comma
                    str.append(eachstring).append(" , ");
                }

                // StringBuffer to String conversion
                String commaseparatedlist = str.toString();

                // By following condition you can remove the last
                // comma
                if (commaseparatedlist.length() > 0)
                    commaseparatedlist
                            = commaseparatedlist.substring(
                            0, commaseparatedlist.length() - 1);

                part_names.setText(commaseparatedlist);
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });





    }

    private void checkText() {
        if(text_send.getText().toString().equals("")){
            btnSend.setVisibility(View.GONE);
        }


    }

    private void onReply(GroupChat getChat) {

        if(!getChat.getSender().equals("LOGS")){
            reply_txt.setText(getChat.getMessage());
            ReplyId = getChat.getSender();
            Replyname = getChat.getSendername();
            reply.setVisibility(View.VISIBLE);
            text_send.isFocused();
            reply_ = true;
        }


    }

    private void hideReplyLayout() {

        reply.setVisibility(View.GONE);
        reply_ = false;

    }


    private void ReplyMessage(String sender,String sendername,String message, String timestamp, String ReplyMessage,String ReplyTo, String Replyname){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String encryptedmessage = aes.Encrypt(message,getApplicationContext());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("sendername", sendername);
        hashMap.put("groupid", groupid);
        hashMap.put("message", encryptedmessage);
        hashMap.put("timestamp", timestamp);
        hashMap.put("reply", "true");
        hashMap.put("replytext", ReplyMessage);
        hashMap.put("replyto",ReplyTo);
        hashMap.put("replyname", Replyname);

        reference.child("GroupChat").child(groupid).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                MediaPlayer mp = MediaPlayer.create(group_messageActivity.this, R.raw.messagesent);
                mp.start();
            }
        });

        final String msg = message;

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                if(notify) {

                    assert group != null;
                    sendNotification(group.getGroupid(), sendername, msg,group.getGroupname(),groupid);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String sender,String sendername, String message,String timestamp)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String encryptedmessage = aes.Encrypt(message,getApplicationContext());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("sendername", sendername);
        hashMap.put("group", groupid);
        hashMap.put("message", encryptedmessage);
        hashMap.put("timestamp", timestamp);
        hashMap.put("reply", "false");

        reference.child("GroupChat").child(groupid).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                MediaPlayer mp = MediaPlayer.create(group_messageActivity.this, R.raw.messagesent);
                mp.start();
            }
        });


        final String msg = message;

        databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(notify) {

                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        Group group = snapshot1.getValue(Group.class);
                        assert group != null;
                        sendNotification(group.getId(), sendername, msg,groupname_,group.getGroupid());
                    }

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void sendNotification(String receiver, String username, String message,String groupname,String group_id){

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");

        chatref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Group group = snapshot1.getValue(Group.class);
                    assert group != null;
                    String receivers = group.getId();
                    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                    Query query = tokens.orderByKey().equalTo(receivers);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Token token = snapshot1.getValue(Token.class);
                                Data data = new Data(firebaseUser.getUid(), R.drawable.ic_notification, username + " : " + message, "New Message from " + groupname,
                                        receivers,"true");
                                assert token != null;
                                Sender sender = new Sender(data, token.getToken());
                                apiService.sendNotification(sender)
                                        .enqueue(new Callback<MyResponse>() {
                                            @Override
                                            public void onResponse(@NotNull Call<MyResponse> call, @NotNull Response<MyResponse> response) {
                                                if (response.code() == 200) {
                                                    assert response.body() != null;
                                                    if (response.body().success != 1) {
                                                        Toast.makeText(group_messageActivity.this, "Failed! Error code 0x08060101", Toast.LENGTH_SHORT).show();
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


                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }

        });

    }




    private void readMessage(String group_id){
        mchat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("GroupChat").child(group_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    GroupChat groupChat = snapshot1.getValue(GroupChat.class);

                    if(group_id.equals(groupid)) {
                        mchat.add(groupChat);

                    }
                    groupMessageAdapter = new GroupMessageAdapter(group_messageActivity.this, mchat);
                    recyclerView.setAdapter(groupMessageAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.create_shortcut) {
            Shortcuts(groupid, groupname_, group_image.getDrawable());
        }
        if(item.getItemId() == R.id.leave_group){

            iOSDialog.Builder
                    .with(group_messageActivity.this)
                    .setTitle(getResources().getString(R.string.leave_group))
                    .setMessage(getResources().getString(R.string.ask_leave_group))
                    .setPositiveText(getResources().getString(R.string.yes))
                    .setPostiveTextColor(getResources().getColor(R.color.red))
                    .setNegativeText(getResources().getString(R.string.cancel))
                    .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                    .onPositiveClicked(new iOSDialogListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            leave(groupid,username);
                        }
                    })
                    .onNegativeClicked(new iOSDialogListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            //Do Nothing
                        }
                    })
                    .isCancellable(true)
                    .build()
                    .show();


        }

        return false;
    }

    private void leave(String groupId,String username) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members").child(firebaseUser.getUid());
        databaseReference.removeValue();

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupId);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("sender", "LOGS");
        hash.put("group", groupId);
        hash.put("reply","false");
        hash.put("message", username + " "+ getResources().getString(R.string.left_group));
        hash.put("timestamp", timestamp);
        databaseReference2.push().setValue(hash);


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(firebaseUser.getUid()).child(groupid);
        databaseReference1.removeValue();

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
                                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, group_messageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("groupid",userid),
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

    }

    public void onBackPressed(){
        Intent intent = new Intent(group_messageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(intent);


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        status("online");
    }


    protected void onPause()
    {
        super.onPause();
        status("offline");

    }

    protected void onDestroy() {
        super.onDestroy();
        status("offline");


    }
}