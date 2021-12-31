package com.margsapp.messageium.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.margsapp.messageium.Main.SendMediaActivity;
import com.margsapp.messageium.Model.Chat;
import com.margsapp.messageium.R;
import com.margsapp.messageium.utils.AES;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.leibnik.chatimageview.ChatImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;
    private final String Username;
    private final String Myname;

    private final Activity activity;


    AES aes;
    FirebaseUser firebaseUser;

    EventListener eventListener;

    public interface EventListener {
        void openImage(String uri, String timestamp, String senderid,String extraid, ImageView chatimageview);
    }

    public void addEventListener(EventListener eventListener){ this.eventListener = eventListener;}
    public void removeEventListener(){ eventListener = null;}



    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int REPLY_TYPE_LEFT = 2;
    public static final int REPLY_TYPE_RIGHT = 3;

    public static final int IMAGE_TYPE_LEFT = 4;
    public static final int IMAGE_TYPE_RIGHT = 5;

    public static final int GIF_TYPE_LEFT = 6;
    public static final int GIF_TYPE_RIGHT = 7;


    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl, Activity activity, String Username,String Myname) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
        this.activity = activity;
        this.Username = Username;
        this.Myname = Myname;
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
        }

        if (viewType == REPLY_TYPE_LEFT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_reply_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }

        if (viewType == IMAGE_TYPE_LEFT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_image_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }
        if (viewType == IMAGE_TYPE_RIGHT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_image_right, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }

        if (viewType == GIF_TYPE_LEFT) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_gif_left, parent, false);
            return new MessageAdapter.ViewHolder(viewGroup);
        }
        else {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.chat_gif_right, parent, false);
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
            }

            if(mChat.get(position).getReply().equals("image")){
                return IMAGE_TYPE_RIGHT;
            }
            if(mChat.get(position).getReply().equals("gif")){
                return GIF_TYPE_RIGHT;
            }

            else{
                return MSG_TYPE_RIGHT;
            }

        }
        else {
            if(mChat.get(position).getReply().equals("true")){
                return REPLY_TYPE_LEFT;
            }

            if(mChat.get(position).getReply().equals("image")){
                return IMAGE_TYPE_LEFT;
            }

            if(mChat.get(position).getReply().equals("gif")){
                return GIF_TYPE_LEFT;
            }

            else{
                return MSG_TYPE_LEFT;
            }

        }



    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);



        holder.username.setVisibility(View.GONE);

        if(chat.getMessage()!=null){
            aes = new AES(mContext);
            String decryptedmessage = aes.Decrypt(chat.getMessage(),mContext);
            holder.show_message.setText(decryptedmessage);
        }



        holder.timestamp.setText(chat.getTimestamp());




        if(chat.getReply().equals("true")){

            if(chat.getReceiver().equals(chat.getReplyto())){
                holder.reply_txt_them.setVisibility(View.GONE);
                holder.reply_txt_us.setVisibility(View.VISIBLE);
                holder.reply_username.setText(Username);
                String decryptedmessage = aes.Decrypt(chat.getReplytext(),mContext);
                holder.reply_txt_us.setText(decryptedmessage);

            }else {
                holder.reply_txt_us.setVisibility(View.GONE);
                holder.reply_txt_them.setVisibility(View.VISIBLE);
                holder.reply_username.setText(Myname);
                String decryptedmessage = aes.Decrypt(chat.getReplytext(),mContext);
                holder.reply_txt_them.setText(decryptedmessage);
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

        if(chat.getReply().equals("image")){

            String uri = chat.getImageurl();
            String timestamp = chat.getTimestamp();
            String senderid = chat.getSender();
            String extraid = chat.getReceiver();
            Picasso.get().load(uri).placeholder(R.drawable.loading).into(holder.chatimage);

            holder.chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage(uri,timestamp,senderid,extraid,holder.chatimage);
                }
            });
        }

        if(chat.getReply().equals("gif")){
            String uri = chat.getImageurl();
            Glide.with(mContext).load(uri).into(holder.gifView);
        }
    }

    private void openImage(String uri, String timestamp, String senderid,String extraid, ImageView chatimageview) {

        eventListener.openImage(uri, timestamp, senderid, extraid, chatimageview);

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

        public ChatImageView chatimage;

        public ImageView gifView;


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

            chatimage = itemView.findViewById(R.id.chatimage);

            gifView = itemView.findViewById(R.id.gifView);
        }



    }




}