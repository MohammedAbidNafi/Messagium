package com.margsapp.messenger.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Adapter.GroupInfoAdapter;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.group_infoActivity;
import com.margsapp.messenger.groupclass.manage_partActivty;

import java.util.ArrayList;
import java.util.List;


public class GroupInfoFragment extends Fragment {

    private List<User> mParticpants;

    private List<Group>update;


    private RecyclerView recyclerView;
    EditText search_user;

    private GroupInfoAdapter groupInfoAdapter;
    String groupname;

    RelativeLayout others;


    CardView group_edit, manage_part;

    ImageButton search_button;

    private TextView created_on,groupname_txt;


    CardView searchUser, leaveGroup;

    boolean search = true;
    private boolean Admin = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        group_infoActivity activity = (group_infoActivity) getActivity();
        assert activity != null;
        groupname = activity.getMyData();

        created_on = view.findViewById(R.id.created_on);


        group_edit = view.findViewById(R.id.group_edit);
        manage_part = view.findViewById(R.id.manage_part);

        groupname_txt = view.findViewById(R.id.groupname_txt);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(groupname);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                groupname_txt.setText(group.getGroupname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchUser = view.findViewById(R.id.search_users_btn);

        leaveGroup = view.findViewById(R.id.leave_group);



        search_button = view.findViewById(R.id.search_button);

        others = view.findViewById(R.id.others);
        mParticpants = new ArrayList<>();

        update = new ArrayList<>();


        update();

        /*
        group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), edit_group_name.class);
                intent.putExtra("groupname", groupname);
                startActivity(intent);
            }
        });
         */

        manage_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), manage_partActivty.class);
                intent.putExtra("groupname", groupname);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                created_on.setText("Created on "+group.getCreatedon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        Animation slidedown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        Animation rotateDown = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_down);
        Animation rotateUp = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_up);

        Animation recyler_down = AnimationUtils.loadAnimation(getContext(), R.anim.recyclerview_anim);
        Animation recycler_up = AnimationUtils.loadAnimation(getContext(), R.anim.recyclerview_anim_up);

        searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search){
                    search_user.setVisibility(View.VISIBLE);
                    search = false;
                    search_user.startAnimation(slidedown);
                    search_button.startAnimation(rotateDown);
                    recyclerView.startAnimation(recyler_down);
                    others.startAnimation(recyler_down);



                }else{

                    search_user.startAnimation(slideUp);
                    search_button.startAnimation(rotateUp);
                    recyclerView.startAnimation(recycler_up);
                    others.startAnimation(recycler_up);
                    new Handler().postDelayed(new Runnable() {
                        // Using handler with postDelayed called runnable run method
                        @Override
                        public void run() {
                            search_user.setVisibility(View.INVISIBLE);
                        }

                    }, 500);
                    search = true;
                }

            }
        });



        search_user = view.findViewById(R.id.search_users);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                SearchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return view;
    }

    private void update(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                update.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Group group = dataSnapshot.getValue(Group.class);
                    update.add(group);
                }

                ReadParticipants();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SearchUser(String toString) {


        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(toString)
                .endAt(toString + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mParticpants.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            for (DataSnapshot snapshot3 : snapshot2.getChildren()){
                                Group group = snapshot3.getValue(Group.class);
                                if(group.getAdmin().equals("true")){
                                    Admin = false;
                                }else {
                                    Admin = true;
                                }

                                if(user.getId().equals(group.getId())){
                                    mParticpants.add(user);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                groupInfoAdapter = new GroupInfoAdapter(getContext(), mParticpants, groupname);
                recyclerView.setAdapter(groupInfoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void ReadParticipants(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (search_user.getText().toString().equals("")) {
                    mParticpants.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        User user = snapshot1.getValue(User.class);
                        for(Group group : update){
                            assert user != null;
                            if(user.getId().equals(group.getId())){
                                mParticpants.add(user);
                            }
                        }
                    }

                    groupInfoAdapter = new GroupInfoAdapter(getContext(), mParticpants, groupname);
                    recyclerView.setAdapter(groupInfoAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}