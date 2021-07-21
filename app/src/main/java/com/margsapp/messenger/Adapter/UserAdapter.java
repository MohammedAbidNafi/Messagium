package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Main.MessageActivity;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.utils.AES;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isChat;

    private final boolean isBlock;

    private final boolean isAdd;


    String theLastMessage;
    String date_time;
    String UnreadMessage;

    Activity activity;

    AES aes;



    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat, boolean isAdd, boolean isBlock,Activity activity) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
        this.isAdd = isAdd;
        this.isBlock = isBlock;
        this.activity = activity;


    }



    private void UnreadMessage(String userid, ImageView unreadview) {

        UnreadMessage = "true";


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getSender().equals(userid)) {
                        UnreadMessage = chat.getIsseen();
                    }


                }

                if ("true".equals(UnreadMessage)) {
                    unreadview.setVisibility(View.GONE);
                } else {
                    unreadview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);


        holder.UsernameText.setText(user.getUsername());

        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_off.setVisibility(View.VISIBLE);
            holder.img_on.setVisibility(View.GONE);
        }

        lastmessage(user.getId(), holder.last_msg,holder.date_lastmsg,mContext);
        UnreadMessage(user.getId(), holder.unread);


        if (user.getImageUrl().equals("default")) {
            holder.profile.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile);
        }

        
        if (isBlock) {
            holder.UnBlock_btn.setVisibility(View.VISIBLE);
        }
        if (!isBlock) {
            holder.UnBlock_btn.setVisibility(View.GONE);
        }

        if (isChat) {

            holder.dt.setVisibility(View.GONE);

        }
        if (!isChat) {
            holder.dt.setText(user.getDt());
            holder.last_msg.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
            holder.date_lastmsg.setVisibility(View.GONE);
            holder.dt.setVisibility(View.VISIBLE);

        }

        holder.UnBlock_btn.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            dialog.setMessage(mContext.getResources().getString(R.string.want_to_block));
            dialog.setPositiveButton(mContext.getResources().getString(R.string.yes), (dialog12, id) -> UnBlock(user.getId()));

            dialog.setNeutralButton(mContext.getResources().getString(R.string.cancel), (dialog1, which) -> {
                //Dont do anything
            });
            androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
            alertDialog.show();



        });


        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));

                }else {
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));

                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(v -> {

            String userid = user.getId();
            OnMessage(userid);
        });

    }







    @Override
    public int getItemCount() {
        return mUsers.size();

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {



        public TextView UsernameText = itemView.findViewById(R.id.username);

        public TextView dt = itemView.findViewById(R.id.dt);
        public ImageView profile;

        private final AppCompatButton addpart;

        private final ImageView img_on;
        private final ImageView img_off;
        private final ImageView unread;
        private final TextView last_msg;
        private final TextView date_lastmsg;
        private final ImageView UnBlock_btn;

        public ViewHolder(View view) {
            super(view);
            profile = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread = itemView.findViewById(R.id.unread);
            UnBlock_btn = itemView.findViewById(R.id.cancel_button);
            date_lastmsg = itemView.findViewById(R.id.date_lastmsg);
            addpart = itemView.findViewById(R.id.addpart);


        }
    }



    private void UnBlock(String userid) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        assert firebaseUser != null;


        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatref.child("friends").setValue("Messaged");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        Toast.makeText(mContext, "User UnBlocked", Toast.LENGTH_SHORT).show();

    }








    private void lastmessage(String userid, TextView lastmsg,TextView date_lastmsg,Context mContext){
        theLastMessage = "default";
        date_time = "default";
        aes = new AES(mContext);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;

                    if(snapshot.exists()){
                        if(chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid)&& chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            date_time = chat.getTimestamp();
                        }

                    }

                }

                if("defalt".equals(date_time)){
                    date_lastmsg.setText("");
                }
                if ("default".equals(theLastMessage)) {
                    lastmsg.setText("No Message");
                } else {
                    String decryptedmessage = aes.Decrypt(theLastMessage, mContext);
                    lastmsg.setText(decryptedmessage);
                    date_lastmsg.setText(date_time);
                }

                theLastMessage = "default";
                date_time = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OnMessage(String userid){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);

                    if(snapshot.exists()){

                        assert chatlist != null;
                        if (chatlist.getFriends().equals("Blocked")) {
                            Toast.makeText(mContext, "You have blocked this user.", Toast.LENGTH_SHORT).show();

                        }
                        else {

                            Intent intent = new Intent(mContext, MessageActivity.class);
                            intent.putExtra("userid", userid);
                            mContext.startActivity(intent);
                            activity.overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);


                        }
                    }else if(!snapshot.exists()){
                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.putExtra("userid", userid);
                        mContext.startActivity(intent);
                        activity.overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                    }



            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



    }




}