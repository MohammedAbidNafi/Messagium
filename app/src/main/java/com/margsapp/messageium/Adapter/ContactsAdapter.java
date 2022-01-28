package com.margsapp.messageium.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.margsapp.messageium.Main.MessageActivity;
import com.margsapp.messageium.Model.Chatlist;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;

    Activity activity;



    public ContactsAdapter(List<User> mUsers, Context context,Activity activity) {
        this.mUsers = mUsers;
        this.mContext = context;
        this.activity = activity;

    }

    @NonNull
    @NotNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_contact_item, parent, false);
        return new ContactsAdapter.ViewHolder(viewGroup);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactsAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.UserName.setText(user.getUsername());

        if (user.getImageUrl().equals("default")) {
            holder.profile.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile);
        }

        holder.phone_number.setText(user.getPhoneno());

        holder.itemView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.onAdapClick));

            }else {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));

            }
            return false;
        });

        holder.itemView.setOnClickListener(v -> {

            String userid = user.getId();
            OnMessage(userid);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView UserName;
        private final TextView phone_number;
        private final CircleImageView profile;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName = itemView.findViewById(R.id.username);
            phone_number = itemView.findViewById(R.id.phone_number);
            profile = itemView.findViewById(R.id.profile_image);

        }


    }

    private void OnMessage(String userid) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);

                if (snapshot.exists()) {

                    assert chatlist != null;
                    if (chatlist.getFriends().equals("Blocked")) {
                        Toast.makeText(mContext, "You have blocked this user.", Toast.LENGTH_SHORT).show();

                    } else {

                        Intent intent = new Intent(mContext, MessageActivity.class);
                        intent.putExtra("userid", userid);
                        mContext.startActivity(intent);
                        activity.overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slider_out_right);


                    }
                } else if (!snapshot.exists()) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid", userid);
                    mContext.startActivity(intent);
                    activity.overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slider_out_right);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}