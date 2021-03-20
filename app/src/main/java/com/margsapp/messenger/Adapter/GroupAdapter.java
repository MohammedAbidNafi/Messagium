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
import com.bumptech.glide.load.ImageHeaderParser;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private final Context mContext;
    private boolean isGroup;
    private final List<Group> mGroups;

    String theLastMessage;
    String UnreadMessage;


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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Group group = mGroups.get(position);
        holder.groupname.setText(group.getGroupname());

        if(group.getImageUrl().equals("default")){
            holder.group_img.setImageResource(R.drawable.groupicon);
        }else {
            Glide.with(mContext).load(group.getImageUrl()).into(holder.group_img);
        }

        
    }



    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView group_img;
        private ImageView unread;

        private TextView last_msg;

        private TextView groupname;


        public ViewHolder(View view){
            super(view);

            group_img = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread = itemView.findViewById(R.id.unread);
            groupname = itemView.findViewById(R.id.username);

        }
    }


}
