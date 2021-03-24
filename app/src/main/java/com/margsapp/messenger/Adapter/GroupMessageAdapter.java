package com.margsapp.messenger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.Model.GroupChat;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.StartActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    private final Context mContext;
    private final List<GroupChat> mGroupChat;


    FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int REPLY_TYPE_LEFT = 2;
    public static final int REPLY_TYPE_RIGHT = 3;

    private String image_url;

    public GroupMessageAdapter(Context mContext, List<GroupChat> mGroupChat) {
        this.mContext = mContext;
        this.mGroupChat = mGroupChat;

    }

    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if (viewType == MSG_TYPE_RIGHT) {

            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(viewGroup);
        }
        if (viewType == MSG_TYPE_LEFT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(viewGroup);

        }

        if (viewType == REPLY_TYPE_RIGHT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_reply_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(viewGroup);
        } else {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_reply_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(viewGroup);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mGroupChat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            if(mGroupChat.get(position).getReply().equals("true")) {
                return REPLY_TYPE_RIGHT;
            }else{
                return MSG_TYPE_RIGHT;
            }

        }
        else {
            if(mGroupChat.get(position).getReply().equals("true")){
                return REPLY_TYPE_LEFT;
            }else{
                return MSG_TYPE_LEFT;
            }

        }



    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageAdapter.ViewHolder holder, int position) {

        GroupChat groupChat = mGroupChat.get(position);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        if(groupChat.getSender().equals(firebaseUser.getUid())){
            holder.username.setVisibility(View.GONE);
        }else {
            holder.username.setVisibility(View.VISIBLE);
        }



        holder.show_message.setText(groupChat.getMessage());

        holder.username.setText(groupChat.getSendername());

        holder.timestamp.setText(groupChat.getTimestamp());

        if(!groupChat.getSender().equals(firebaseUser.getUid())){
            updateImage(groupChat.getSender(), holder.groupImage);
        }







        if(groupChat.getReply().equals("true")){

            if(groupChat.getSender().equals(groupChat.getReplyto())){
                holder.reply_txt_them.setVisibility(View.GONE);
                holder.reply_txt_us.setVisibility(View.VISIBLE);
                holder.reply_username.setText(groupChat.getReplyname());
                holder.reply_txt_us.setText(groupChat.getReplytext());

            }else {
                holder.reply_txt_us.setVisibility(View.GONE);
                holder.reply_txt_them.setVisibility(View.VISIBLE);
                holder.reply_username.setText(groupChat.getReplyname());
                holder.reply_txt_them.setText(groupChat.getReplytext());
            }


        }


    }

    private void updateImage(String id, CircleImageView circleImageView) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                String image_url = user.getImageUrl();
                if(image_url.equals("default"))
                {
                    circleImageView.setImageResource(R.drawable.user);
                }else {
                    Glide.with(mContext).load(image_url).into(circleImageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
            }
        });






    }

    @Override
    public int getItemCount() {
        return mGroupChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView groupImage;
        TextView groupname;
        TextView participants_name;

        public TextView show_message, username;

        public TextView reply_txt_them;
        public TextView reply_txt_us;
        public TextView reply_username;
        public TextView timestamp;

        public ViewHolder(View view){
            super(view);
            groupImage = itemView.findViewById(R.id.profile_image);
            groupname = itemView.findViewById(R.id.groupname);
            participants_name = itemView.findViewById(R.id.participants_name);
            show_message = itemView.findViewById(R.id.show_message);
            timestamp = itemView.findViewById(R.id.timestamp);
            username = itemView.findViewById(R.id.username);
            reply_username = itemView.findViewById(R.id.reply_username);




            reply_txt_them = itemView.findViewById(R.id.replytextthem);
            reply_txt_us = itemView.findViewById(R.id.replytextus);


        }
    }
}
