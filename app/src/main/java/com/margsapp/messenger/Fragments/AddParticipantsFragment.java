package com.margsapp.messenger.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Adapter.AddPartAdapter;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.AddParticipants;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class AddParticipantsFragment extends Fragment implements AddPartAdapter.EventListener {

    private RecyclerView recyclerView;
    private List<User> mUsers;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    private List<Chatlist>usersList;

    private AddPartAdapter addPartAdapter;

    public String groupId, MyName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_participants, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //rotateLoading = view.findViewById(R.id.loading);

        usersList = new ArrayList<>();

        FirebaseUser firebaseUser1 = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser1 != null;
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser1.getUid());
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                MyName = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }



                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AddParticipants activity = (AddParticipants) getActivity();
        assert activity != null;
        groupId = activity.getMyData();


        return view;
    }



    private void chatList() {

        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    for (Chatlist chatlist : usersList){
                        assert user != null;
                        if(user.getId().equals(chatlist.getId())){
                            if(chatlist.getFriends().equals("Messaged")){
                                mUsers.add(user);
                            }

                        }


                    }

                }



                AddPartAdapter addPartAdapter = new AddPartAdapter(getContext(), mUsers, groupId,false);
                addPartAdapter.addEventListener(AddParticipantsFragment.this);
                recyclerView.setAdapter(addPartAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public void AddParticipant(String id, String username, RotateLoading rotateLoading, Context mContext, ImageView remove) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(id).child(groupId);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupid", groupId);
        hashMap.put("admin","false");
        databaseReference1.setValue(hashMap);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupId);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("sender", "LOGS");
        hash.put("group", groupId);
        hash.put("reply","false");
        hash.put("message", MyName + " Added "+ username);
        hash.put("timestamp", timestamp);
        databaseReference2.push().setValue(hash);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupId).child("members").child(id);
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("id", id);
        hashMap1.put("user_name", username);
        hashMap1.put("admin","false");
        databaseReference.setValue(hashMap1).addOnCompleteListener(task -> {
            rotateLoading.stop();
            rotateLoading.setVisibility(View.GONE);


            Toast.makeText(mContext,username + getResources().getString(R.string.is_added),Toast.LENGTH_SHORT).show();

            remove.setVisibility(View.VISIBLE);
        });


    }

    @Override
    public void RemoveParticipant() {

    }

    public void onDestroy() {
        super.onDestroy();
        addPartAdapter.removeEventListener();
    }
}