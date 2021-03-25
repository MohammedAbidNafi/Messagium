package com.margsapp.messenger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ViewHolder>{

    private  Context mContext;
    private  List<Group> mGroup;

    public GroupInfoAdapter(Context mContext, List<Group>mGroup) {
        this.mContext = mContext;
        this.mGroup = mGroup;
    }

    @NonNull
    @Override
    public GroupInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.groupinfo_item, parent, false);
        return new GroupInfoAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupInfoAdapter.ViewHolder holder, int position) {






    }

    @Override
    public int getItemCount() {
        return mGroup.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView UserName;
        private TextView dt;
        private CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }


}
