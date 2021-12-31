package com.margsapp.messageium.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messageium.Model.Group;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.R;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddPartAdapter extends RecyclerView.Adapter<AddPartAdapter.ViewHolder> {

    private final Context mContext;
    private final List<User> mUsers;

    private final boolean managepart;
    private final String groupid;

    EventListener listener;





    public interface EventListener {
        void AddParticipant(String id, String username, RotateLoading rotateLoading, Context mContext,ImageView remove);
        void RemoveParticipant();
    }



    public void addEventListener(EventListener listener){
        this.listener = listener;
    }

    public void removeEventListener(){
        listener = null;
    }



    public AddPartAdapter(Context mContext, List<User> mUsers,String groupid, boolean managepart) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.groupid = groupid;
        this.managepart = managepart;

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


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Group group = snapshot1.getValue(Group.class);
                    assert group != null;
                    if(group.getId().equals(user.getId())){
                        holder.addpart_btn.setVisibility(View.GONE);
                        holder.remove.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.addpart_btn.setOnClickListener(v -> {
            String Variable = user.getId();
            String Variable2 = user.getUsername();
            holder.rotateLoading.setVisibility(View.VISIBLE);
            holder.addpart_btn.setVisibility(View.GONE);
            holder.rotateLoading.start();
            //Send call that method and send the variablethat variable to Fragment
            // Using handler with postDelayed called runnable run method
            new Handler().postDelayed(() -> listener.AddParticipant(Variable, Variable2, holder.rotateLoading, mContext,holder.remove),  1100); // wait for 5 seconds
        });

        holder.remove.setOnClickListener(v -> {
            String idd = user.getId();
            String name = user.getUsername();


            iOSDialog.Builder
                    .with(mContext)
                    .setTitle("Remove")
                    .setMessage(mContext.getResources().getString(R.string.ask_remove))
                    .setPositiveText(mContext.getResources().getString(R.string.yes))
                    .setPostiveTextColor(mContext.getResources().getColor(R.color.red))
                    .setNegativeText(mContext.getResources().getString(R.string.cancel))
                    .setNegativeTextColor(mContext.getResources().getColor(R.color.company_blue))
                    .onPositiveClicked(new iOSDialogListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            remove(idd,name,groupid);
                            holder.remove.setVisibility(View.GONE);
                            holder.addpart_btn.setVisibility(View.VISIBLE);
                        }
                    })
                    .onNegativeClicked(new iOSDialogListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            //Do Nothing
                        }
                    })
                    .isCancellable(true)
                    .build()
                    .show();




        });

    }

    private void remove(String id, String name,String groupId) {

        listener.RemoveParticipant();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(id).child(groupid);
        databaseReference1.removeValue();

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupId);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("sender", "LOGS");
        hash.put("group", groupId);
        hash.put("reply","false");
        hash.put("message", name + " has been removed from group.");
        hash.put("timestamp", timestamp);
        databaseReference2.push().setValue(hash);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members").child(id);
        databaseReference.removeValue();

        Toast.makeText(mContext, name + " has been removed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView dp;
        private final ImageView unread;
        private final ImageView remove;


        public TextView dt;
        private final TextView username;
        RotateLoading rotateLoading;

        AppCompatButton addpart_btn;


        public ViewHolder(View view){
            super(view);

            remove = itemView.findViewById(R.id.remove);
            rotateLoading = itemView.findViewById(R.id.loading);
            dp = itemView.findViewById(R.id.profile_image);
            dt = itemView.findViewById(R.id.dt);
            unread = itemView.findViewById(R.id.unread);
            username = itemView.findViewById(R.id.username);
            addpart_btn = itemView.findViewById(R.id.addpart);

        }
    }
}