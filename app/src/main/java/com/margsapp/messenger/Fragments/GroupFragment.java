package com.margsapp.messenger.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.margsapp.messenger.Adapter.GroupAdapter;
import com.margsapp.messenger.Adapter.UserAdapter;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.GroupList;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.Notifications.Token;
import com.margsapp.messenger.R;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {

    private RecyclerView recyclerView;

    public GroupAdapter groupAdapter;

    private List<Group> mGroup;
    private List<GroupList>groupList;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroup = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("GroupList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void groupList() {

        mGroup = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGroup.clear();




                recyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}