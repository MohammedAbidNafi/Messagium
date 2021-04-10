package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.margsapp.messenger.MainActivity;
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.R;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.margsapp.messenger.AboutActivity.TEXT1;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageUrl;


    FirebaseUser firebaseUser;



    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public static final int REPLY_TYPE_LEFT = 2;
    public static final int REPLY_TYPE_RIGHT = 3;

    private SharedPreferences sharedPreferences;

    private byte encryptionKey[] = {1, 15, 4, 7, 20, -96, -66, -74, -95, 115, (byte) 157, 104, 3, -25, 114, -94};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;



    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl,SharedPreferences sharedPreferences) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
        this.sharedPreferences = sharedPreferences;

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




        /*
        SecretKey aesKey = null;
        try {
            aesKey = generateAESKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final IvParameterSpec ivParameterSpec = generateIv();





        if(chat.getEncrypted().equals("true")){
            try {
                String decryptedMessage;
                String encryptedMessage = chat.getMessage();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    decryptedMessage = decryptMessage(encryptedMessage, aesKey, ivParameterSpec);
                    holder.show_message.setText(decryptedMessage);
                }else {
                    holder.show_message.setText(chat.getMessage() + "This message is encrypted please update your Android");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            holder.show_message.setText(chat.getMessage());
        }

         */

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

            String read = sharedPreferences.getString(TEXT1, "");

            if(read.equals("0")){
                holder.txt_seen.setText("Delivered");
            }
            else {
                if (chat.getIsseen().equals("true")) {
                    holder.txt_seen.setText("Read");
                }else {
                    holder.txt_seen.setText("Delivered");
                }

            }
        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    static String decryptMessage(final String encryptedMessage, final SecretKey aesKey, final IvParameterSpec iv) throws Exception {
        final byte[] encryptedData = Base64.getDecoder().decode(encryptedMessage.getBytes(StandardCharsets.UTF_8));
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
        final byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    static SecretKey generateAESKey() throws Exception {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // For AES256
        return keyGenerator.generateKey();
    }

    static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
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
