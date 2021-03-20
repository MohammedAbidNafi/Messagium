package com.margsapp.messenger.Adapter;

import android.content.Context;
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

import java.util.EventListener;
import java.util.List;

public class AddPartAdapter extends RecyclerView.Adapter<AddPartAdapter.ViewHolder> {

    private  Context mContext;
    private  List<User> mUsers;

    EventListener listener;



    public interface EventListener {
        void AddParticipant(String id);
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
                String Variable = user.getId();//This is the variable that should pass
                //Send call that method and send the variablethat variable to Fragment
                listener.AddParticipant(Variable);
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

        AppCompatButton addpart_btn;


        public ViewHolder(View view){
            super(view);

            dp = itemView.findViewById(R.id.profile_image);
            dt = itemView.findViewById(R.id.dt);
            unread = itemView.findViewById(R.id.unread);
            username = itemView.findViewById(R.id.username);
            addpart_btn = itemView.findViewById(R.id.addpart);

        }
    }
}
