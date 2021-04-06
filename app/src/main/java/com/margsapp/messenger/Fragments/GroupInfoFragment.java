package com.margsapp.messenger.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Adapter.GroupInfoAdapter;
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Chatlist;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.group_infoActivity;
import com.margsapp.messenger.groupclass.manage_partActivty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GroupInfoFragment extends Fragment implements GroupInfoAdapter.EventListener {

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

                assert group != null;
                groupname_txt.setText(group.getGroupname());
                created_on.setText("Created on "+group.getCreatedon());
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

                assert firebaseUser != null;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(firebaseUser.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group group = snapshot.getValue(Group.class);

                        assert group != null;
                        if(group.getAdmin().equals("true")){
                            Intent intent = new Intent(getContext(), manage_partActivty.class);
                            intent.putExtra("groupname", groupname);
                            startActivity(intent);
                        }else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                            dialog.setTitle("Not an Admin.");
                            dialog.setMessage("You have to be Admin to make changes in group.");
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        leaveGroup = view.findViewById(R.id.leave_group);

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setMessage("Are you sure you want to leave the group?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        leave();
                    }
                });
                dialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dont do anything
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

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
                            search_user.setVisibility(View.GONE);
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

    private void leave() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(firebaseUser.getUid());
        databaseReference.removeValue();


        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(firebaseUser.getUid()).child(groupname);
        databaseReference1.removeValue();

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

                    groupInfoAdapter = new GroupInfoAdapter(getContext(), mParticpants, groupname);
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
    public void onOptions(String userid, String Username, String groupname) {



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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                if(group.getAdmin().equals("true")){
                    make_admin_txt.setText("Group Admin");
                    make_admin_txt.setTextColor(getContext().getResources().getColor(R.color.blacktext));
                }else {
                    make_admin_txt.setText("Make Admin");
                    make_admin_txt.setTextColor(getContext().getResources().getColor(R.color.company_blue));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        remove_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setMessage("Are you sure you want to remove this user?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        remove(userid,Username,groupname);

                    }
                });
                dialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dont do anything
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });



        make_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(userid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group group = snapshot.getValue(Group.class);
                        assert group != null;
                       if(!group.getAdmin().equals("true")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                            dialog.setMessage("Are you sure you want to make this user Admin? (Note this is a irreversible process)");
                            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(userid);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("admin", "true");
                                    databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(getContext(),group_infoActivity.class).putExtra("groupname",groupname));
                                        }
                                    });

                                }
                            });

                            dialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Dont do anything
                                }
                            });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMessage(userid);
            }
        });

        voice_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"This feature is not available yet.", Toast.LENGTH_SHORT).show();
            }
        });

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"This feature is not available yet.", Toast.LENGTH_SHORT).show();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"This feature is not available yet.", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void remove(String userid, String username,String groupname) {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(userid).child(groupname);
        databaseReference1.removeValue();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname).child("members").child(userid);
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getContext(),group_infoActivity.class).putExtra("groupname",groupname));
            }
        });

        Toast.makeText(getContext(), username + " has been removed",Toast.LENGTH_SHORT).show();


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
                        Toast.makeText(getContext(), "You have blocked this user.", Toast.LENGTH_SHORT).show();

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