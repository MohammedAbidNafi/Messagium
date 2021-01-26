package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
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
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;


    FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;





    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;

    }



    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }
        else {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);

        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        holder.timestamp.setText(chat.getTimestamp());

        if(imageUrl.equals("default"))
        {
            holder.profile.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(imageUrl).into(holder.profile);
        }

        if(position == mChat.size()-1) {
            if (chat.getIsseen().equals("true")) {
                holder.txt_seen.setText("Read");
            } else {
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

        public TextView show_message = itemView.findViewById(R.id.show_message);
        public ImageView profile;
        public TextView txt_seen;
        public TextView timestamp;
        public ViewHolder(View view){
            super(view);

            profile = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
