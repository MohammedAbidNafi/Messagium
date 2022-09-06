package com.margsapp.messageium.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.factor.bouncy.BouncyRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.margsapp.messageium.Adapter.UserAdapter;
import com.margsapp.messageium.Model.Chatlist;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.Notifications.Token;
import com.margsapp.messageium.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;


public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;

    private BouncyRecyclerView recyclerView;
    private List<User> mUsers;

    private List<Chatlist>usersList;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    AppCompatEditText search_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chats, container, false);





        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setFlingAnimationSize(0.3f);
        recyclerView.setOverscrollAnimationSize(0.3f);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        loadMessages();

        updateToken(FirebaseInstanceId.getInstance().getToken());


        search_user = view.findViewById(R.id.search_users);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                loadMessage_seacrh(charSequence.toString());

                checkText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }



    private void loadMessages(){


        new Thread(){
            @Override
            public void run(){

                try {

                    loadMessage();
                }
                catch (final Exception exception){
                    Timber.i("Exception in Thread LoadMessages");
                }


            }
        }.start();



    }

    protected void loadMessage(){

        Query query = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
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

    }





    private void chatList() {

        mUsers = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
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

                            if(chatlist.getFriends().equals("Requested")){
                                mUsers.add(user);
                            }

                        }


                    }

                }



                userAdapter = new UserAdapter(getContext(), mUsers, true, false,getActivity());
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkText() {
        if(search_user.getText().toString().equals("")){
           loadMessages();
        }
    }


    protected void loadMessage_seacrh(String toString){

        Query query = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).orderByChild("time");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList_search(toString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void chatList_search(String toString) {

        mUsers = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(toString)
                .endAt(toString+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
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

                            if(chatlist.getFriends().equals("Requested")){
                                mUsers.add(user);
                            }

                        }


                    }

                }



                userAdapter = new UserAdapter(getContext(), mUsers, true, false,getActivity());
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }






}