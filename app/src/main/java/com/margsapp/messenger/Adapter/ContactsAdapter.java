package com.margsapp.messenger.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;

    public ContactsAdapter(List<User> mUsers, Context context) {
        this.mUsers = mUsers;
        this.mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.user_contact_item, parent, false);
        return new ContactsAdapter.ViewHolder(viewGroup);
    }

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

}