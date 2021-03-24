package com.margsapp.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.SplashActivity;
import com.margsapp.messenger.StartActivity;
import com.victor.loading.rotate.RotateLoading;

import java.util.EventListener;
import java.util.List;

public class AddPartAdapter extends RecyclerView.Adapter<AddPartAdapter.ViewHolder> {

    private final Context mContext;
    private  List<User> mUsers;

    EventListener listener;



    public interface EventListener {
        void AddParticipant(String id, String username, RotateLoading rotateLoading, Context mContext);
    }

    public void addEventListener(EventListener listener){
        this.listener = listener;
    }

    public void removeEventListener(){
        listener = null;
    }


    public AddPartAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;

    }



    @NonNull
    @Override
    public AddPartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.add_part_item, parent, false);
        return new AddPartAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPartAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        holder.dt.setText(user.getDt());

        if (user.getImageUrl().equals("default")) {
            holder.dp.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.dp);
        }

        holder.addpart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Variable = user.getId();
                String Variable2 = user.getUsername();
                holder.rotateLoading.setVisibility(View.VISIBLE);
                holder.addpart_btn.setVisibility(View.GONE);
                holder.rotateLoading.start();
                //Send call that method and send the variablethat variable to Fragment
                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        listener.AddParticipant(Variable, Variable2, holder.rotateLoading, mContext);
                    }
                },  1*1100); // wait for 5 seconds
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView dp;
        private ImageView unread;


        public TextView dt;
        private TextView username;
        RotateLoading rotateLoading;

        AppCompatButton addpart_btn;


        public ViewHolder(View view){
            super(view);

            rotateLoading = itemView.findViewById(R.id.loading);
            dp = itemView.findViewById(R.id.profile_image);
            dt = itemView.findViewById(R.id.dt);
            unread = itemView.findViewById(R.id.unread);
            username = itemView.findViewById(R.id.username);
            addpart_btn = itemView.findViewById(R.id.addpart);

        }
    }
}
