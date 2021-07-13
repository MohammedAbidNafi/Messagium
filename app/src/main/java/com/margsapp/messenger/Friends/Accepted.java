package com.margsapp.messenger.Friends;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Settings.Chat_settings;
import com.margsapp.messenger.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Accepted extends AppCompatActivity {


    FirebaseUser firebaseUser;

    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted);

        SlidrInterface slidrInterface = Slidr.attach(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.accepted));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {

            startActivity(new Intent(Accepted.this, Chat_settings.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
            finish();
            overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        final ViewPager viewPager = findViewById(R.id.viewPager);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Accepted.ViewPageAdapter viewPageAdapter = new Accepted.ViewPageAdapter(getSupportFragmentManager());

                viewPageAdapter.addFragment(new AcceptedFragment(), "Accepted");

                viewPager.setAdapter(viewPageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void onBackPressed(){
        startActivity(new Intent(Accepted.this, Chat_settings.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
        finish();
        overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
    }

    static class ViewPageAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}