package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isChat;

    private boolean isBlock;

    private boolean isAdd;

    private final boolean unreadbool = true;



    String theLastMessage;
    String UnreadMessage;



    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat, boolean isAdd, boolean isBlock) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
        this.isAdd = isAdd;
        this.isBlock = isBlock;







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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.UsernameText.setText(user.getUsername());
        holder.dt.setText(user.getDt());

        if (user.getImageUrl().equals("default")) {
            holder.profile.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile);
        }
        if (unreadbool) {
            UnreadMessage(user.getId(), holder.unread);
        }

        if (isBlock) {
            holder.UnBlock_btn.setVisibility(View.VISIBLE);
        }
        if (!isBlock) {
            holder.UnBlock_btn.setVisibility(View.GONE);
        }

        if (isChat) {
            lastmessage(user.getId(), holder.last_msg);
            holder.dt.setVisibility(View.GONE);

        }
        if (!isChat) {
            holder.last_msg.setVisibility(View.GONE);
            holder.dt.setVisibility(View.VISIBLE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }

        holder.UnBlock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                dialog.setMessage("Are you sure you want to Unblock this user?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        UnBlock(user.getId());


                    }
                });

                dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dont do anything
                    }
                });
                androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
                alertDialog.show();



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

        private final ImageView img_on;
        private final ImageView img_off;
        private final ImageView unread;
        private final TextView last_msg;

        private final ImageView addFriend;
        private final ImageView UnBlock_btn;

        public ViewHolder(View view) {
            super(view);
            profile = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread = itemView.findViewById(R.id.unread);
            addFriend = itemView.findViewById(R.id.addFriend);
            UnBlock_btn = itemView.findViewById(R.id.cancel_button);


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








    private void lastmessage(String userid, TextView lastmsg){
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid)&& chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                    }

                }
                if ("default".equals(theLastMessage)) {
                    lastmsg.setText("No Message");
                } else {
                    lastmsg.setText(theLastMessage);
                }

                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OnMessage(String userid){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);


                    if(!chatlist.getFriends().equals("Blocked")){
                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.putExtra("userid", userid);
                        mContext.startActivity(intent);

                    }else if (chatlist.getFriends().equals("Blocked")) {
                        Toast.makeText(mContext, "You have blocked this user.", Toast.LENGTH_SHORT).show();

                    }









                    }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



    }


}
