package com.margsapp.messenger.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.margsapp.messenger.Adapter.GroupAdapter;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.GroupList;
import com.margsapp.messenger.Notifications.Token;
import com.margsapp.messenger.R;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {

    private RecyclerView recyclerView;

    public GroupAdapter groupAdapter;

    private List<Group> mGroup;
    private List<GroupList>groupLists;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        groupLists = new ArrayList<>();
        update();

        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void update() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Grouplist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupLists.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    GroupList groupList = snapshot1.getValue(GroupList.class);
                    groupLists.add(groupList);
                }


                groupList();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void groupList() {

        mGroup = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Group");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGroup.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Group group = snapshot1.getValue(Group.class);
                    for(GroupList groupList : groupLists){
                        assert group != null;
                        if(group.getGroupid().equals(groupList.getGroupid())){
                            mGroup.add(group);
                        }
                    }

                }



                groupAdapter = new GroupAdapter(getContext(), true, mGroup);
                recyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}