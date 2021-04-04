package com.margsapp.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.User;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static com.margsapp.messenger.AboutActivity.TEXT;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    AppCompatButton login,register,phone;

    FirebaseUser firebaseUser;

    FirebaseAuth firebaseAuth;

    private InterstitialAd mInterstitialAd;

    private SignInButton signInButton;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onStart() {
        //Check if user is logged
        super.onStart();



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Authentication", 0);
            String bio = sharedPreferences.getString(TEXT, "");


            if (bio.equals("1")) {

                BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
                switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | DEVICE_CREDENTIAL)) {

                    // this means we can use biometric sensor
                    case BiometricManager.BIOMETRIC_SUCCESS:

                        break;

                    // this means that the device doesn't have fingerprint sensor
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                        break;

                    // this means that biometric sensor is not available
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:

                        break;

                    // this means that the device doesn't contain your fingerprint
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:

                        break;
                    case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:

                        break;
                    case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:

                        break;
                }
                // creating a variable for our Executor
                Executor executor = ContextCompat.getMainExecutor(this);
                // this will give us result of AUTHENTICATION
                final BiometricPrompt biometricPrompt = new BiometricPrompt(StartActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Login Success.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
                // creating a variable for our promptInfo
                // BIOMETRIC DIALOG
                final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Authentication")
                        .setDescription("Use your fingerprint to login ")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | DEVICE_CREDENTIAL).build();

                biometricPrompt.authenticate(promptInfo);

            } else {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/6412055406", adRequest, new InterstitialAdLoadCallback() {
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



        signInButton = findViewById(R.id.signin);
        //login = findViewById(R.id.login);
      //  register = findViewById(R.id.register);
        phone = findViewById(R.id.phone);

        firebaseAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("246831665234-urv5fcie13sd3lsiubbv044su3e8m29c.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(StartActivity.this,googleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,100);

            }
        });

        /*
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginAcitivity.class));
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(StartActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

         */

        /*
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(StartActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

         */

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, PhoneAuthActivity.class));
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(StartActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==100){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(signInAccountTask.isSuccessful()){
                String s = "Google Authentication successful";

                displayToast(s);

                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);

                    if(googleSignInAccount != null){
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            assert firebaseUser != null;
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                            String userid = firebaseUser.getDisplayName();
                                            String username = firebaseUser.getDisplayName();
                                            String imageurl;

                                            Calendar calendar = Calendar.getInstance();
                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                                            String timestamp = simpleDateFormat.format(calendar.getTime());

                                            Uri imageuri = firebaseUser.getPhotoUrl();
                                            assert imageuri != null;
                                            imageurl = imageuri.toString();
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if(!snapshot.exists()){
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("id", userid);
                                                        hashMap.put("username", username);
                                                        hashMap.put("imageURL", imageurl);
                                                        hashMap.put("joined_on",timestamp);
                                                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(new Intent(StartActivity.this,google_setupActivity.class)
                                                                        .putExtra("method","google")
                                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                            }
                                                        });
                                                    }else {
                                                        startActivity(new Intent(StartActivity.this,google_setupActivity.class)
                                                                .putExtra("method","google")
                                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });




                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}