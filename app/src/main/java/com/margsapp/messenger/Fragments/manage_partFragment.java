package com.margsapp.messenger.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.manage_partActivty;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;


public class manage_partFragment extends Fragment implements AddPartAdapter.EventListener{

    private RecyclerView recyclerView;
    private List<Group> mUsers;

    FirebaseUser firebaseUser;

    private List<User> usersList;

    public String groupname;

    public manage_partFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_part, container, false);
        manage_partActivty activity = (manage_partActivty) getActivity();
        assert activity != null;
        groupname = activity.getMyData();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mUsers = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatList();




        return view;
    }


    @Override
    public void AddParticipant(String id, String username, RotateLoading rotateLoading, Context mContext, ImageView remove) {}

    public void RemoveParticipant(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).commitNow();
            getFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void chatList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    mUsers.add(group);
                }

                ReadParticipants();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ReadParticipants(){
        usersList = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    for(Group group : mUsers){
                        assert user != null;
                        assert firebaseUser != null;
                        if(!firebaseUser.getUid().equals(user.getId())){
                            if(user.getId().equals(group.getId())){
                                usersList.add(user);
                            }
                        }

                    }
                }

                AddPartAdapter addPartAdapter = new AddPartAdapter(getContext(), usersList, groupname,true);
                addPartAdapter.addEventListener(manage_partFragment.this);
                recyclerView.setAdapter(addPartAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
