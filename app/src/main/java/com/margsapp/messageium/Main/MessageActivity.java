package com.margsapp.messageium.Main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.TransitionManager;

import com.ashiqurrahman.easyvidchat.VidChat;
import com.ashiqurrahman.easyvidchat.data.CustomTURNSTUNConfig;
import com.ashiqurrahman.easyvidchat.data.VidChatConfig;
import com.bumptech.glide.Glide;
import com.factor.bouncy.BouncyRecyclerView;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.drawables.ImageFormat;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
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
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messageium.Adapter.MessageAdapter;
import com.margsapp.messageium.ImageView.chat_image_viewActivity;
import com.margsapp.messageium.ImageView.personal_dpActivity;
import com.margsapp.messageium.Model.Chat;
import com.margsapp.messageium.Model.Chatlist;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.Notifications.APIService;
import com.margsapp.messageium.Notifications.Client;
import com.margsapp.messageium.Notifications.Data;
import com.margsapp.messageium.Notifications.MyResponse;
import com.margsapp.messageium.Notifications.Sender;
import com.margsapp.messageium.Notifications.Token;
import com.margsapp.messageium.R;
import com.margsapp.messageium.VideoCall.CallActivity;
import com.margsapp.messageium.reply.SwipeController;
import com.margsapp.messageium.utils.AES;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.margsapp.messageium.Settings.CustomiseActivity.THEME;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.EventListener {

    private static final String TAG = "MESSAGE ACTIVITY" ;
    private static final String WALLPAPER = "WALLPAPER";
    private static final int PERMISSION_REQUEST_CODE = 1232;
    private static final int CALLREQUESTCODE = 1234;

    CircleImageView profileImage;
    TextView username, statusText, reply_txt;

    private AES aes;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,txt_database;

    ImageView addImage;

    ImageButton btnSend, btn_cancel_reply;
    EditText text_send;

    AppCompatButton btn_accept, btn_reject, btn_block;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    BouncyRecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    APIService apiService;

    String userid;
    String ReplyId;
    String Username;
    String Myname;


    String imageUrl;
    String blocked;

    Dialog dialog;




    boolean entertosend = false;
    ConstraintLayout reply;
    RelativeLayout warning,editor;


    boolean reply_ = false;

    boolean checklist = false;

    boolean notify = false;
    ShortcutManager shortcutManager;

    private SlidrInterface slidrInterface;

    public static Handler seenMessageHandler = new Handler();
    public static Handler readMessageHandler = new Handler();
    public static Handler sendMessageHandler = new Handler();


    ConstraintLayout mainbackgound;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onStart() {
        super.onStart();

        mainbackgound = findViewById(R.id.mainbackgound);

        SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
        String wallpaper = sharedPreferences.getString(WALLPAPER, "");

        switch(wallpaper){


            case "null":
                mainbackgound.setBackgroundColor(getResources().getColor(R.color.background));
                Log.d("imageBackground", "null");
                break;

            case "dark1":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.dark1));
                Log.d("imageBackground", "dark1");
                break;

            case "dark2":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.dark2));
                Log.d("imageBackground", "dark2");
                break;

            case "dark3":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.dark3));
                Log.d("imageBackground", "dark3");
                break;

            case "light1":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.light1));
                Log.d("imageBackground", "light1");
                break;

            case "light2":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.light2));
                Log.d("imageBackground", "light2");
                break;

            case "light3":
                mainbackgound.setBackground(getResources().getDrawable(R.drawable.light3));
                Log.d("imageBackground", "light3");
                break;
        }

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

        TransitionManager.beginDelayedTransition(editor);
        dialog = new Dialog(MessageActivity.this);
    }

    @SuppressLint("GetInstance")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        SlidrConfig config = new SlidrConfig.Builder()
                .edge(true)
                .edgeSize(0.2f)
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {

                    }

                    @Override
                    public void onSlideChange(float percent) {

                    }

                    @Override
                    public void onSlideOpened() {

                    }

                    @Override
                    public boolean onSlideClosed() {
                        return false;
                    }
                })// The % of the screen that counts as the edge, default 18%
                .build();


        slidrInterface = Slidr.attach(this, config);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager.class);
        }


        addImage = findViewById(R.id.addimage);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMediaSelect();
                editor.setVisibility(View.GONE);
            }
        });
        toolbar.setNavigationOnClickListener(v -> {

            finish();
            overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);
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



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setFlingAnimationSize(0.3f);
        recyclerView.setOverscrollAnimationSize(0.3f);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        SwipeController swipeController = new SwipeController(this, position -> onReply(mchat.get(position)));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        aes = new AES(this);

        btn_cancel_reply.setOnClickListener(v -> hideReplyLayout());

        profileImage.setOnClickListener(v -> {
            String data = imageUrl;
            Intent intent = new Intent(MessageActivity.this, personal_dpActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profileImage, "imageTransition");
            intent.putExtra("data", data);
            intent.putExtra("userid", userid);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MessageActivity.this, pairs);
            startActivity(intent, options.toBundle());

            new Handler().postDelayed(this::finish, 1000);

        });


        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
                btnSend.setVisibility(View.VISIBLE);
                entertosend = true;
                txt_database = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                txt_database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("typingto", userid);
                        snapshot.getRef().updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                checkText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        new Thread(() -> {

            CheckFirst();

            DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebaseUser.getUid())
                    .child(userid);

            chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    if (snapshot.exists()) {
                        assert chatlist != null;
                        blocked = chatlist.getFriends();
                        if (chatlist.getFriends().equals("Requested")) {
                            editor.setVisibility(View.GONE);
                            warning.setVisibility(View.VISIBLE);

                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    Myname = user.getUsername();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }).start();





        btnSend.setOnClickListener(v -> {

            notify = true;
            String isseen = "false";

            String msg = text_send.getText().toString();

            new Thread(() -> {
                if (!msg.equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                    String timestamp = simpleDateFormat.format(calendar.getTime());



                    if (reply_) {
                        String Reply = reply_txt.getText().toString();
                        ReplyMessage(firebaseUser.getUid(), userid, msg, timestamp, isseen, Reply, ReplyId, reference,checklist);
                    }
                    if (!reply_) {
                        sendMessage(firebaseUser.getUid(), userid, msg, timestamp, isseen, reference,checklist);
                    }


                } else {
                    Toast.makeText(MessageActivity.this, "You cant send empty message Error code 0x08040101", Toast.LENGTH_SHORT).show();
                }

                runOnUiThread(() -> {
                    text_send.setText("");

                    hideReplyLayout();

                });

            }).start();



        });


        btn_accept.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebaseUser.getUid())
                    .child(userid);
            Long server_timestamp = new Date().getTime();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("friends", "Messaged");
            hashMap.put("time", server_timestamp);
            databaseReference.getRef().updateChildren(hashMap);
            warning.setVisibility(View.GONE);
            editor.setVisibility(View.VISIBLE);
            Toast.makeText(MessageActivity.this, "Request Accepted!", Toast.LENGTH_SHORT).show();
        });

        btn_reject.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Request Rejected!", Toast.LENGTH_SHORT).show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(firebaseUser.getUid())
                    .child(userid);
            warning.setVisibility(View.GONE);
            editor.setVisibility(View.GONE);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("friends", "Rejected");
            databaseReference.getRef().updateChildren(hashMap).addOnCompleteListener(task -> {
                warning.setVisibility(View.GONE);
                editor.setVisibility(View.GONE);
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });


        });

        btn_block.setOnClickListener(v -> {


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
        });



        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                Username = user.getUsername();


                if (snapshot.exists()) {
                    if (user.getTypingto().equals(firebaseUser.getUid())) {
                        statusText.setText(getResources().getString(R.string.typing));
                    } else {
                        statusText.setText(user.getStatus());
                    }
                }


                imageUrl = user.getImageUrl();

                if (imageUrl.equals("default")) {
                    profileImage.setImageResource(R.drawable.user);

                } else {
                    Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
                }

                readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        MessageActivity.seenMessageHandler.post(new Runnable() {
            @Override
            public void run() {
                seenMessage(userid);
            }
        });



    }

    private void CheckFirst() {

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(firebaseUser.getUid());
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                if(!snapshot.exists()){
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                    chatRefReceiver.child("friends").setValue("Requested");
                    checklist = true;

                }

                if(snapshot.exists()){
                    assert chatlist != null;
                    if(chatlist.getFriends().equals("Rejected")){
                        chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                        chatRefReceiver.child("friends").setValue("Requested");
                        checklist = true;
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void checkText() {
        if(text_send.getText().toString().equals("")){
            btnSend.setVisibility(View.GONE);
            entertosend = false;
            txt_database = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            txt_database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("typingto", "");
                    snapshot.getRef().updateChildren(hashMap);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    private void onReply(Chat getChat) {

        String decryptedmessage = aes.Decrypt(getChat.getMessage(),getApplicationContext());
        reply_txt.setText(decryptedmessage);
        reply.setVisibility(View.VISIBLE);
        ReplyId = getChat.getSender();
        text_send.isFocused();
        reply_ = true;

    }

    private void hideReplyLayout() {

        reply.setVisibility(View.GONE);
        reply_ = false;

    }


    private void seenMessage(String userid) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
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






    private void ReplyMessage(String sender, String receiver, String message, String timestamp, String isseen,String ReplyMessage,String ReplyTo,DatabaseReference reference,boolean checklist_){

        String encryptedmessage = aes.Encrypt(message, this);

        Map hashmap = new HashMap();
        hashmap.put("sender", sender);
        hashmap.put("receiver", receiver);
        hashmap.put("message", encryptedmessage);
        hashmap.put("isseen", isseen);
        hashmap.put("timestamp", timestamp);
        hashmap.put("reply", "true");
        hashmap.put("replytext", ReplyMessage);
        hashmap.put("replyto",ReplyTo);






        reference.child("Chats").push().setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                /*
                MediaPlayer mp = MediaPlayer.create(MessageActivity.this, R.raw.messagesent);
                mp.start();

                 */
            }
        });





        if(checklist_){

            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                    .child(receiver)
                    .child(firebaseUser.getUid());
            chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                        chatRefReceiver.child("friends").setValue("Requested");
                        checklist = false;
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
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

    private void sendMessage(String sender, String receiver, String message,String timestamp,String isseen,DatabaseReference reference,boolean checklist_)
    {

        MessageActivity.sendMessageHandler.post(new Runnable() {
            @Override
            public void run() {
                String encryptedmessage = aes.Encrypt(message, getApplicationContext());


                Map hashmap = new HashMap();
                hashmap.put("sender", sender);
                hashmap.put("receiver", receiver);
                hashmap.put("message", encryptedmessage);
                hashmap.put("isseen", isseen);
                hashmap.put("timestamp", timestamp);
                hashmap.put("reply", "false");



                reference.child("Chats").push().setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        /*
                        MediaPlayer mp = MediaPlayer.create(MessageActivity.this, R.raw.messagesent);
                        mp.start();

                         */
                    }
                });




                if(checklist_){


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
                                checklist = false;

                            }

                            if(snapshot.exists()){
                                assert chatlist != null;
                                if(chatlist.getFriends().equals("Rejected")){
                                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                                    chatRefReceiver.child("friends").setValue("Requested");
                                    checklist = false;
                                }
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            checklist = false;

                        }
                    });


                }

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
                        Long server_timestamp = new Date().getTime();
                        chatref.child("time").setValue(server_timestamp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(notify) {
                            assert user != null;
                            sendNotification(receiver, user.getUsername(), message);
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });





    }


    private void sendNotification(String receiver, String username, String message){

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(firebaseUser.getUid());

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);

                if(snapshot.exists()){
                    assert chatlist != null;
                    if(!chatlist.getFriends().equals("Blocked")) {
                        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
                        Query query = tokens.orderByKey().equalTo(receiver);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Token token = snapshot1.getValue(Token.class);
                                    Data data = new Data(firebaseUser.getUid(), R.drawable.ic_notification, username + ":" + message, "New Message",
                                            userid,"false");
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

        MessageActivity.readMessageHandler.post(new Runnable() {
            @Override
            public void run(){
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



                            messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageUrl,MessageActivity.this,Username,Myname);
                            messageAdapter.addEventListener(MessageActivity.this);
                            recyclerView.setAdapter(messageAdapter);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }




    public void onMediaSelect(){


        AppCompatButton gallery,camera;

        CardView gif;

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




        gallery = dialog.findViewById(R.id.gallery);

        gif = dialog.findViewById(R.id.gif);

        camera = dialog.findViewById(R.id.camera);

        cancel = dialog.findViewById(R.id.cancel);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TedBottomPicker.with(MessageActivity.this)
                        .setSpacing(2)
                        .showGalleryTile(false)
                        .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                // here is selected image uri

                                String imageuri = uri.toString();
                                Timber.d(imageuri);
                                Intent intent = new Intent(getApplicationContext(), SendImageActivity.class);
                                intent.putExtra("imageUri", imageuri);
                                intent.putExtra("userid", userid);

                                startActivity(intent);
                            }
                        });

                dialog.dismiss();
                editor.setVisibility(View.VISIBLE);
            }
        });


        gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Giphy.INSTANCE.configure(MessageActivity.this, "oRWoXZrudMifFWvkqcnDVymhHKYiyh34", false);

                final GPHSettings settings = new GPHSettings();

                settings.setImageFormat(ImageFormat.WEBP);
                final GiphyDialogFragment dialog_ = GiphyDialogFragment.Companion.newInstance(settings);
                GiphyDialogFragment.GifSelectionListener listener = null;
                dialog_.setGifSelectionListener(listener);
                dialog_.show(getSupportFragmentManager(), "giphy_dialog");

                dialog_.setGifSelectionListener(new GiphyDialogFragment.GifSelectionListener() {
                    @Override
                    public void onGifSelected(@NonNull Media media, @androidx.annotation.Nullable String s, @NonNull GPHContentType gphContentType) {
                        //Your user tapped a GIF
                        Intent intent = new Intent(MessageActivity.this, SendMediaActivity.class);
                        intent.putExtra("userid", userid);

                        String uri = Objects.requireNonNull(media.getImages().getOriginal()).getGifUrl();
                        intent.putExtra("imageUri", uri);
                        startActivity(intent);

                    }

                    @Override
                    public void onDismissed(@NonNull GPHContentType gphContentType) {
                        //Your user dismissed the dialog without selecting a GIF
                    }

                    @Override
                    public void didSearchTerm(@NonNull String s) {
                        //Callback for search terms
                    }
                });



                editor.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming soon!",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });






        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
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


                iOSDialog.Builder
                        .with(this)
                        .setTitle(getResources().getString(R.string.block))
                        .setMessage(getResources().getString(R.string.want_to_block))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeText(getResources().getString(R.string.cancel))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                Block();
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


                break;

            case R.id.video_call:
                VideoCall();
                break;

            case R.id.create_shortcut:
                Shortcuts(userid,username.getText().toString().trim(),profileImage.getDrawable());
                break;

            case R.id.delete_chat:
                iOSDialog.Builder
                        .with(this)
                        .setTitle(getResources().getString(R.string.delete_chat))
                        .setMessage(getString(R.string.ask_to_delete_chat))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeText(getResources().getString(R.string.no))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                DeleteChat(userid);
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

                break;

        }

        return false;
    }

    private void VideoCall() {

        iOSDialog.Builder
                .with(this)
                .setTitle(getResources().getString(R.string.video_call))
                .setMessage(getString(R.string.ask_to_call))
                .setPositiveText(getResources().getString(R.string.yes))
                .setPostiveTextColor(getResources().getColor(R.color.red))
                .setNegativeText(getResources().getString(R.string.no))
                .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                .onPositiveClicked(new iOSDialogListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        call();
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

    private void call() {


        String otherID = userid;

        startActivity(new Intent(MessageActivity.this, CallActivity.class).putExtra("userid", otherID));

         /*

        VidChat.INSTANCE.requestVideoChatPermissions(MessageActivity.this,PERMISSION_REQUEST_CODE);
        startActivityForResult(VidChat.INSTANCE.getCallingIntent(this, "ABCDEF"),CALLREQUESTCODE);

        CustomTURNSTUNConfig config = new CustomTURNSTUNConfig(
                "turn:103.147.168.11:3478",
                "abid",
                "1234",
                "stun:103.147.168.11:3478"
        );

        VidChatConfig.INSTANCE.setCustomTURNSTUNConfig(config);
        // setting up custom room server url
        VidChatConfig.INSTANCE.setCustomRoomServerUrl("https://mycustomroomserverurl");

          */
    }

    private void DeleteChat(String userid) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            Intent openMainActivity = new Intent(MessageActivity.this, MainActivity.class);
            openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(openMainActivity, 0);
        });

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
        hashMap.put("typingto","");
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
    }






    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);


    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }


    protected void onPause() {
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
        messageAdapter.removeEventListener();
    }


    @Override
    public void openImage(String uri, String timestamp, String senderid, String extraid, ImageView chatimageview) {

        Intent intent = new Intent(MessageActivity.this, chat_image_viewActivity.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(chatimageview, "chatImage");
        intent.putExtra("imageuri",uri);
        intent.putExtra("senderid",senderid);
        intent.putExtra("extraid",extraid);
        intent.putExtra("timestamp",timestamp);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MessageActivity.this, pairs);
        startActivity(intent, options.toBundle());



    }


}