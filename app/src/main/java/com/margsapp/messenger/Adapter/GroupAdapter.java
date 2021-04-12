package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.GroupChat;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.group_messageActivity;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private final Context mContext;
    private final boolean isGroup;
    private final List<Group> mGroups;

    String theLastMessage;



    public GroupAdapter(Context mContext, boolean isGroup, List<Group> mGroups) {
        this.mContext = mContext;
        this.isGroup = isGroup;
        this.mGroups = mGroups;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(viewGroup);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Group group = mGroups.get(position);


        holder.groupname.setText(group.getGroupname());
        String group_name = group.getGroupname();
        lastmessage(group_name, holder.last_msg);

        if(group.getImageUrl().equals("group_default")){
            holder.group_img.setImageResource(R.drawable.groupicon);
        }else {
            Glide.with(mContext).load(group.getImageUrl()).into(holder.group_img);
        }


        holder.itemView.setOnTouchListener((v, event) -> {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
            String groupid = group.getGroupid();
            launch(groupid);

        });

        
    }

    private void launch(String groupid){


        Intent intent = new Intent(mContext, group_messageActivity.class);
        intent.putExtra("groupid", groupid);
        mContext.startActivity(intent);

    }

    private void lastmessage(String groupid, TextView last_msg) {
        theLastMessage = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupid);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    GroupChat groupChat = snapshot1.getValue(GroupChat.class);
                    assert groupChat != null;
                    if(groupChat.getGroupid().equals(groupid)) {
                        theLastMessage = groupChat.getMessage();
                    }

                }
                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                }

                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView group_img;
        private final ImageView unread;

        private final TextView last_msg;

        private final TextView groupname;


        public ViewHolder(View view){
            super(view);

            group_img = itemView.findViewById(R.id.group_img);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread = itemView.findViewById(R.id.unread);
            groupname = itemView.findViewById(R.id.groupname);

        }
    }


}
