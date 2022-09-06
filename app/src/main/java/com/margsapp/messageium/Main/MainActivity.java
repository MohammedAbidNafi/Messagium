package com.margsapp.messageium.Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messageium.Authentication.StartActivity;
import com.margsapp.messageium.BuildConfig;
import com.margsapp.messageium.Fragments.ChatsFragment;
import com.margsapp.messageium.Fragments.SettingsFragment;
import com.margsapp.messageium.groupclass.GroupFragment;
import com.margsapp.messageium.Model.Chat;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.R;
import com.margsapp.messageium.Settings.edit_profile;
import com.margsapp.messageium.ImageView.main_dpActivity;
import com.margsapp.messageium.groupclass.create_groupActivity;
import com.margsapp.messageium.utils.ApplicationLifecycleHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import timber.log.Timber;

import static com.margsapp.messageium.Settings.CustomiseActivity.THEME;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN ACTIVITY";
    CircleImageView DP;
    TextView username;
    AppCompatEditText search_chat;


    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String imageurl;

    String versionName = BuildConfig.VERSION_NAME;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    TextView network_txt;

    private ProgressBar network_check;



    ViewPager viewPager;

    PageNavigationView tab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(handler);
        }
        registerComponentCallbacks(handler);

        SharedPreferences sharedPreferences = getSharedPreferences("lang_settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("lang","");
        setLocale(language);

        viewPager = findViewById(R.id.viewPager);
        tab = findViewById(R.id.tab);

        tab.setBackgroundColor(getResources().getColor(R.color.main_element));






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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");


        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                boolean connected = snapshot1.getValue(Boolean.class);
                if (connected) {
                    network_check.setVisibility(View.GONE);
                    network_txt.setVisibility(View.GONE);
                    DP.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);

                } else {
                    network_check.setVisibility(View.VISIBLE);
                    network_txt.setVisibility(View.VISIBLE);
                    DP.setVisibility(View.GONE);
                    username.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Timber.tag(TAG).w("Listener was cancelled");
            }
        });

        network_check = findViewById(R.id.network_check);
        network_txt = findViewById(R.id.network_txt);



        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        firebaseAuth = FirebaseAuth.getInstance();

        DP = findViewById(R.id.DP);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        status("online");


        NavigationController navigationController = tab.material()
                .addItem(R.drawable.ic_baseline_person_outline_24,"Chats" )
                .addItem(R.drawable.ic_baseline_people_outline_24, "Groups")
                .addItem(R.drawable.ic_baseline_settings_24, "Settings")
                .setDefaultColor(getResources().getColor(R.color.black_text))
                .build();


        DP.setOnClickListener(v -> {
            String data = imageurl;

            Intent intent = new Intent(MainActivity.this, main_dpActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(DP, "main_image");
            intent.putExtra("data",data);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(intent, options.toBundle());

        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                imageurl = user.getImageUrl();
                username.setText(user.getUsername());
                if (imageurl.equals("default")) {
                    Glide.with(getApplicationContext()).load(R.drawable.user).into(DP);
                } else {

                    Glide.with(getApplicationContext()).load(imageurl).into(DP);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        final TabLayout tabLayout = findViewById(R.id.tablayout);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.addFragment(new ChatsFragment(), getResources().getString(R.string.chat));
        viewPageAdapter.addFragment(new GroupFragment(), getResources().getString(R.string.group));
        viewPageAdapter.addFragment(new SettingsFragment(),getResources().getString(R.string.settings));
        viewPager.setAdapter(viewPageAdapter);
        navigationController.setupWithViewPager(viewPager);


        /*
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chat chat = snapshot1.getValue(Chat.class);

                    if(snapshot1.exists()){
                        assert chat != null;
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsseen().equals("false")) {
                            unread++;
                        }
                    }

                }


                if (unread == 0) {
                    viewPageAdapter.addFragment(new ChatsFragment(), getResources().getString(R.string.chat));
                } else {
                    viewPageAdapter.addFragment(new ChatsFragment(), getResources().getString(R.string.chat)+"("+unread+")");
                }


                viewPageAdapter.addFragment(new GroupFragment(), getResources().getString(R.string.group));


                viewPager.setAdapter(viewPageAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

         */
        


    }





    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            /*
            case R.id.logout:



                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


                return true;

            case R.id.edit:
                startActivity(new Intent(MainActivity.this, edit_profile.class));
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

                return false;

            case R.id.create_group:
                startActivity(new Intent(MainActivity.this, create_groupActivity.class));
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);


                return true;

             */


            case R.id.addchat:
                startActivity(new Intent(MainActivity.this, FindUsersActivity.class));
                overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

                return true;
        }

        return false;
    }



    public void onBackPressed(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
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

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        hashMap.put("version_name", versionName);
        hashMap.put("lastseen", timestamp);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }


    protected void onRestart() {
        super.onRestart();
        status("online");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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





}