package com.margsapp.messenger.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messenger.Adapter.GroupInfoAdapter;
import com.margsapp.messenger.Authentication.VerifyOTP;
import com.margsapp.messenger.Main.MessageActivity;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.edit_group_name;
import com.margsapp.messenger.groupclass.group_infoActivity;
import com.margsapp.messenger.groupclass.manage_partActivty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GroupInfoFragment extends Fragment implements GroupInfoAdapter.EventListener {

    private List<User> mParticpants;

    private List<Group>update;


    private RecyclerView recyclerView;
    EditText search_user;

    private GroupInfoAdapter groupInfoAdapter;
    String groupid, Username;

    RelativeLayout others;


    CardView group_edit, manage_part;

    ImageView search_button;

    private TextView created_on,groupname_txt;


    CardView searchUser, leaveGroup;

    Dialog dialog;

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

        dialog = new Dialog(getContext());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        group_infoActivity activity = (group_infoActivity) getActivity();
        assert activity != null;
        groupid = activity.getMyData();

        created_on = view.findViewById(R.id.created_on);


        group_edit = view.findViewById(R.id.group_edit);
        manage_part = view.findViewById(R.id.manage_part);

        groupname_txt = view.findViewById(R.id.groupname_txt);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(groupid);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                assert group != null;
                groupname_txt.setText(group.getGroupname());

                created_on.setText(getResources().getString(R.string.created_on) + " "+group.getCreatedon());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Username = user.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchUser = view.findViewById(R.id.search_users_btn);




        search_button = view.findViewById(R.id.search_button);

        others = view.findViewById(R.id.others);
        mParticpants = new ArrayList<>();

        update = new ArrayList<>();


        update();




        group_edit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), edit_group_name.class);
            intent.putExtra("groupid", groupid);
            intent.putExtra("username", Username);
            startActivity(intent);
        });


        manage_part.setOnClickListener(v -> {

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members").child(firebaseUser.getUid());
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group group = snapshot.getValue(Group.class);

                    assert group != null;
                    if(group.getAdmin().equals("true")){
                        Intent intent = new Intent(getContext(), manage_partActivty.class);
                        intent.putExtra("groupid", groupid);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                    }else {
                        iOSDialog.Builder
                                .with(requireActivity())
                                .setTitle(requireContext().getResources().getString(R.string.not_an_admin))
                                .setMessage(requireContext().getResources().getString(R.string.admin_message))
                                .setPositiveText(requireContext().getResources().getString(R.string.ok))
                                .setPostiveTextColor(requireContext().getResources().getColor(R.color.red))
                                .setNegativeText(requireContext().getResources().getString(R.string.cancel))
                                .setNegativeTextColor(requireContext().getResources().getColor(R.color.company_blue))
                                .onPositiveClicked(new iOSDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {

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


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        leaveGroup = view.findViewById(R.id.leave_group);

        leaveGroup.setOnClickListener(v -> {

            iOSDialog.Builder
                    .with(requireContext())
                    .setTitle(getResources().getString(R.string.leave_group))
                    .setMessage(getResources().getString(R.string.ask_leave_group))
                    .setPositiveText(getResources().getString(R.string.yes))
                    .setPostiveTextColor(getResources().getColor(R.color.red))
                    .setNegativeText(getResources().getString(R.string.cancel))
                    .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                    .onPositiveClicked(new iOSDialogListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            leave(groupid,Username);
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

        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        Animation slidedown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        Animation rotateDown = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_down);
        Animation rotateUp = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_up);

        Animation recyler_down = AnimationUtils.loadAnimation(getContext(), R.anim.recyclerview_anim);
        Animation recycler_up = AnimationUtils.loadAnimation(getContext(), R.anim.recyclerview_anim_up);

        searchUser.setOnClickListener(v -> {
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
                // Using handler with postDelayed called runnable run method
                new Handler().postDelayed(() -> search_user.setVisibility(View.GONE), 500);
                search = true;
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

    private void leave(String groupId,String username) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members").child(firebaseUser.getUid());
        databaseReference.removeValue();

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupId);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("sender", "LOGS");
        hash.put("group", groupId);
        hash.put("reply","false");
        hash.put("message", username + " "+ getResources().getString(R.string.left_group));
        hash.put("timestamp", timestamp);
        databaseReference2.push().setValue(hash);


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(firebaseUser.getUid()).child(groupid);
        databaseReference1.removeValue();

    }

    private void update(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");
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
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(groupid).child("members");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            for (DataSnapshot snapshot3 : snapshot2.getChildren()){
                                Group group = snapshot3.getValue(Group.class);
                                assert group != null;
                                Admin = !group.getAdmin().equals("true");

                                assert user != null;
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
                groupInfoAdapter = new GroupInfoAdapter(getContext(), mParticpants, groupid);
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
                mParticpants.clear();
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

                    groupInfoAdapter = new GroupInfoAdapter(getContext(), mParticpants, groupid);
                    groupInfoAdapter.addEventListener(GroupInfoFragment.this);
                    recyclerView.setAdapter(groupInfoAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onOptions(String userid, String Username, String groupid_) {



        CardView  info,make_admin,message,voice_call,video_call,cancel;
        TextView make_admin_txt;

        AppCompatButton username,remove_group;
        dialog.setContentView(R.layout.group_info_pop_up);

        //username_txt = dialog.findViewById(R.id.username_txt);
        username = dialog.findViewById(R.id.username);
        info = dialog.findViewById(R.id.info);
        make_admin = dialog.findViewById(R.id.make_admin);
        message = dialog.findViewById(R.id.message);
        voice_call = dialog.findViewById(R.id.voice_call);
        video_call = dialog.findViewById(R.id.video_call);
        remove_group = dialog.findViewById(R.id.remove_group);
        cancel = dialog.findViewById(R.id.cancel);
        make_admin_txt = dialog.findViewById(R.id.make_admin_txt);





        username.setText(Username);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid_).child("members").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                assert group != null;
                if(group.getAdmin().equals("true")){
                    make_admin_txt.setText(getResources().getString(R.string.group_admin));
                    make_admin_txt.setTextColor(requireContext().getResources().getColor(R.color.blacktext));

                }else {
                    make_admin_txt.setText(getResources().getString(R.string.make_admin));
                    make_admin_txt.setTextColor(requireContext().getResources().getColor(R.color.company_blue));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        remove_group.setOnClickListener(v -> {
            assert firebaseUser != null;
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Group").child(groupid_).child("members").child(firebaseUser.getUid());
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group group = snapshot.getValue(Group.class);
                    assert group != null;
                    if(group.getAdmin().equals("true")){
                        iOSDialog.Builder
                                .with(requireContext())
                                .setTitle(getResources().getString(R.string.remove_from_group))
                                .setMessage(getResources().getString(R.string.ask_remove))
                                .setPositiveText(getResources().getString(R.string.yes))
                                .setPostiveTextColor(getResources().getColor(R.color.red))
                                .setNegativeText(getResources().getString(R.string.cancel))
                                .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                                .onPositiveClicked(new iOSDialogListener() {
                                    @Override
                                    public void onClick(Dialog dialog) {
                                        remove(userid,Username,groupid_);
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

                    }else {
                        Toast.makeText(getContext(),getResources().getString(R.string.not_an_admin),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });



        make_admin.setOnClickListener(v -> {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Group").child(groupid_).child("members").child(userid);
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Group group = snapshot.getValue(Group.class);
                    assert group != null;
                   if(!group.getAdmin().equals("true")) {
                       iOSDialog.Builder
                               .with(requireContext())
                               .setTitle(getResources().getString(R.string.group_admin))
                               .setMessage(getResources().getString(R.string.ask_to_make))
                               .setPositiveText(getResources().getString(R.string.yes))
                               .setPostiveTextColor(getResources().getColor(R.color.red))
                               .setNegativeText(getResources().getString(R.string.cancel))
                               .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                               .onPositiveClicked(new iOSDialogListener() {
                                   @Override
                                   public void onClick(Dialog dialog) {
                                       Calendar calendar = Calendar.getInstance();
                                       @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                                       String timestamp = simpleDateFormat.format(calendar.getTime());

                                       DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupid_);
                                       HashMap<String, Object> hash = new HashMap<>();
                                       hash.put("sender", "LOGS");
                                       hash.put("group", groupid_);
                                       hash.put("reply","false");
                                       hash.put("message", Username + getResources().getString(R.string.now_admin));
                                       hash.put("timestamp", timestamp);
                                       databaseReference2.push().setValue(hash);

                                       DatabaseReference databaseReference11 = FirebaseDatabase.getInstance().getReference("Group").child(groupid_).child("members").child(userid);
                                       HashMap<String, Object> map = new HashMap<>();
                                       map.put("admin", "true");
                                       databaseReference11.updateChildren(map).addOnCompleteListener(task -> startActivity(new Intent(getContext(),group_infoActivity.class).putExtra("groupid",groupid_)));

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


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





        });

        message.setOnClickListener(v -> onMessage(userid));

        voice_call.setOnClickListener(v -> Toast.makeText(getContext(),getResources().getString(R.string.feature_not_availble), Toast.LENGTH_SHORT).show());

        video_call.setOnClickListener(v -> Toast.makeText(getContext(),getResources().getString(R.string.feature_not_availble), Toast.LENGTH_SHORT).show());

        info.setOnClickListener(v -> Toast.makeText(getContext(),getResources().getString(R.string.feature_not_availble), Toast.LENGTH_SHORT).show());

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }



    private void remove(String userid, String username,String groupid_) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(userid).child(groupid_);
        databaseReference1.removeValue();


        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupid_);
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("sender", "LOGS");
        hash.put("group", groupid_);
        hash.put("reply","false");
        hash.put("message", username + getResources().getString(R.string.log_removed));
        hash.put("timestamp", timestamp);
        databaseReference2.push().setValue(hash);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupid_).child("members").child(userid);
        databaseReference.removeValue().addOnCompleteListener(task -> startActivity(new Intent(getContext(),group_infoActivity.class).putExtra("groupid",groupid_)));




    }

    private void onMessage(String userid) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);

                if(snapshot.exists()){

                    assert chatlist != null;
                    if (chatlist.getFriends().equals("Blocked")) {
                        Toast.makeText(getContext(), getResources().getString(R.string.blocked_this_user), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra("userid", userid);
                        startActivity(intent);
                    }
                }else if(!snapshot.exists()){
                    Intent intent = new Intent(getContext(), MessageActivity.class);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    public void onDestroy() {
        super.onDestroy();
        groupInfoAdapter.removeEventListener();
    }

}