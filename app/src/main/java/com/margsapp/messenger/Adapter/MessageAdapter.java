package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.R;

import java.util.List;

import static com.margsapp.messenger.Settings.AboutActivity.TEXT1;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;


    FirebaseUser firebaseUser;



    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int REPLY_TYPE_LEFT = 2;
    public static final int REPLY_TYPE_RIGHT = 3;






    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;


    }







    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {

            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }
        if (viewType == MSG_TYPE_LEFT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);

        }

        if (viewType == REPLY_TYPE_RIGHT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_reply_right, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        } else {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_reply_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }



    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            if(mChat.get(position).getReply().equals("true")) {
                return REPLY_TYPE_RIGHT;
            }else{
                return MSG_TYPE_RIGHT;
            }

        }
        else {
            if(mChat.get(position).getReply().equals("true")){
                return REPLY_TYPE_LEFT;
            }else{
                return MSG_TYPE_LEFT;
            }

        }



    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.username.setVisibility(View.GONE);

        holder.show_message.setText(chat.getMessage());
        holder.timestamp.setText(chat.getTimestamp());


        if(chat.getReply().equals("true")){

            if(chat.getReceiver().equals(chat.getReplyto())){
                holder.reply_txt_them.setVisibility(View.GONE);
                holder.reply_txt_us.setVisibility(View.VISIBLE);
                holder.reply_username.setText(chat.getReplyname());
                holder.reply_txt_us.setText(chat.getReplytext());

            }else {
                holder.reply_txt_us.setVisibility(View.GONE);
                holder.reply_txt_them.setVisibility(View.VISIBLE);
                holder.reply_username.setText(chat.getReplyname());
                holder.reply_txt_them.setText(chat.getReplytext());
            }


        }


        if(imageUrl.equals("default"))
        {
            holder.profile.setImageResource(R.drawable.user);
        }else {
            Glide.with(mContext).load(imageUrl).into(holder.profile);
        }

        if(position == mChat.size()-1) {


            if (chat.getIsseen().equals("true")) {
                holder.txt_seen.setText("Read");
            }else if(chat.getIsseen().equals("false")) {
                holder.txt_seen.setText("Delivered");
            }


        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile;
        public TextView txt_seen;
        public TextView timestamp;

        public TextView username;

        public TextView reply_txt_them;
        public TextView reply_txt_us;
        public TextView reply_username;
        public LinearLayout linearLayout;


        public ViewHolder(View view){
            super(view);



            show_message = itemView.findViewById(R.id.show_message);
            profile = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            timestamp = itemView.findViewById(R.id.timestamp);
            username = itemView.findViewById(R.id.username);
            linearLayout = itemView.findViewById(R.id.layout_reply);
            reply_username = itemView.findViewById(R.id.reply_username);

            reply_txt_them = itemView.findViewById(R.id.replytextthem);
            reply_txt_us = itemView.findViewById(R.id.replytextus);
        }



    }




}
