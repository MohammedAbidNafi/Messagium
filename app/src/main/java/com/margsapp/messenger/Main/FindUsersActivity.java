package com.margsapp.messenger.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.margsapp.messenger.Fragments.ContactsFragment;
import com.margsapp.messenger.Fragments.UsersFragment;
import com.margsapp.messenger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import static com.margsapp.messenger.Settings.CustomiseActivity.THEME;

public class FindUsersActivity extends AppCompatActivity {

    private static final String TAG = "FindUsersActivity";


    FirebaseUser firebaseUser;



    private InterstitialAd mInterstitialAd;
    ViewPager viewPager;


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("theme", 0);
        String Theme = preferences.getString(THEME, "");
        if(Theme.equals("2")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(Theme.equals("1")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(Theme.equals("0")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.add_chat));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);


        /*

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/4782730227", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

         */

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(FindUsersActivity.this, MainActivity.class));
            if (mInterstitialAd != null) {
                mInterstitialAd.show(FindUsersActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        final ViewPager viewPager = findViewById(R.id.viewPager);
        final TabLayout tabLayout = findViewById(R.id.tablayout);

        FindUsersActivity.ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new UsersFragment(), "Users");
        viewPageAdapter.addFragment(new ContactsFragment(), "Contacts");
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewPageAdapter);





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
        FirebaseUser firebaseUserStatus = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUserStatus != null;
        DatabaseReference statusdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserStatus.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        statusdatabaseReference.updateChildren(hashMap);

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

    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    public void onBackPressed(){
        startActivity(new Intent(FindUsersActivity.this, MainActivity.class));
        if (mInterstitialAd != null) {
            mInterstitialAd.show(FindUsersActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

}