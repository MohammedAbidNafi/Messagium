package com.margsapp.messenger.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;
    private final String groupname;

    EventListener listener;

    public interface EventListener {
        void onOptions(String userid, String Username, String groupname);
    }

    public void addEventListener(GroupInfoAdapter.EventListener listener) {
        this.listener = listener;
    }

    public void removeEventListener() {
        listener = null;
    }

    public GroupInfoAdapter(Context mContext, List<User> mUsers, String groupname) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.groupname = groupname;
    }

    @NonNull
    @Override
    public GroupInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.groupinfo_item, parent, false);
        return new GroupInfoAdapter.ViewHolder(viewGroup);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull GroupInfoAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.UserName.setText(user.getUsername());
        holder.dt.setText(user.getDt());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getImageUrl().equals("default")) {
            holder.profile.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(user.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                if (snapshot.exists()) {
                    assert group != null;
                    if (group.getAdmin().equals("true")) {
                        holder.admin.setVisibility(View.VISIBLE);
                    } else {
                        holder.admin.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        assert firebaseUser != null;
        if (!user.getId().equals(firebaseUser.getUid())) {

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(event.getAction()== MotionEvent.ACTION_DOWN){
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));
                    }else if(event.getAction()==MotionEvent.ACTION_UP){
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
                    }
                    return false;
                }
            });
            holder.itemView.setOnClickListener(v -> {
              // holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));
                listener.onOptions(user.getId(), user.getUsername(), groupname);
            });

        }


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView UserName;
        private final TextView dt;
        private final CircleImageView profile;
        private final TextView admin;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName = itemView.findViewById(R.id.username);
            dt = itemView.findViewById(R.id.dt);
            profile = itemView.findViewById(R.id.profile_image);
            admin = itemView.findViewById(R.id.admin);


        }
    }

}