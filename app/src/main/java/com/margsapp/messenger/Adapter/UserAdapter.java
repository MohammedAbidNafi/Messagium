package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.margsapp.messenger.FindUsersActivity;
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Chat;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private  Context mContext;
    private  List<User> mUsers;
    private  boolean isChat;

    private boolean unreadbool = true;





    String theLastMessage;
    String UnreadMessage;

    String ActualUnreadMessage;



    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;



    }

    private void UnreadMessage(String userid, ImageView unreadview){

         UnreadMessage = "true";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);

                    if(chat.getSender().equals(userid)) {

                        UnreadMessage = chat.getIsseen();

                    }


                }

                if("true".equals(UnreadMessage)){
                    unreadview.setVisibility(View.GONE);
                }
                else{
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
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_item, parent,false);
        return new UserAdapter.ViewHolder(viewGroup);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.UsernameText.setText(user.getUsername());
        holder.dt.setText(user.getDt());

        if (user.getImageUrl().equals("default"))
        {
            holder.profile.setImageResource(R.drawable.user);
        }
        else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile);
        }

        if(unreadbool){

            UnreadMessage(user.getId(), holder.unread);


        }


        if(isChat){
            lastmessage(user.getId(), holder.last_msg);
            holder.dt.setVisibility(View.GONE);

        }if(!isChat) {
            holder.last_msg.setVisibility(View.GONE);
            holder.dt.setVisibility(View.VISIBLE);
        }

        if(isChat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        }
        else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String ourid = firebaseUser.getUid();
            String userid = user.getId();
            OnMessage(userid, ourid);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView UsernameText = itemView.findViewById(R.id.username);

        public TextView dt = itemView.findViewById(R.id.dt);
        public ImageView profile;

        private final ImageView img_on;
        private final ImageView img_off;
        private final ImageView unread;
        private final TextView last_msg;

        public ViewHolder(View view){
            super(view);
            profile = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread = itemView.findViewById(R.id.unread);
        }
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

    private void OnMessage(String userid, String ourid){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);
                        if (userid.equals(chatlist.getId())) {
                            if (chatlist.getFriends().equals("Messaged")) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("userid", userid);
                                mContext.startActivity(intent);

                            }

                            if (userid.equals(chatlist.getId())){

                                if (chatlist.getFriends().equals("Blocked")) {
                                    Toast.makeText(mContext, "You have blocked this user.", Toast.LENGTH_SHORT).show();

                                }

                            }


                        }

                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
