package com.margsapp.messenger.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    String codeBySystem;
    PinView pinFromUser;
    String phoneNo;

    public TextView description;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);


        pinFromUser = findViewById(R.id.pin_view);
        description = findViewById(R.id.description);



        phoneNo = getIntent().getStringExtra("phoneNo");

        description.setText(getResources().getString(R.string.OTP_DESC) + phoneNo);

        TextView resend = findViewById(R.id.resend);
        TextView wrongnumber = findViewById(R.id.wrongnumber);
        resend.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(VerifyOTP.this);
            dialog.setMessage(getResources().getString(R.string.Ask_OTP_again));
            dialog.setPositiveButton(getResources().getString(R.string.yes), (dialog12, id) -> {
                sendVerificationCodeToUser(phoneNo);

                Toast.makeText(VerifyOTP.this, getResources().getString(R.string.another_code_sent) + phoneNo,Toast.LENGTH_SHORT).show();
            });
            dialog.setNeutralButton(getResources().getString(R.string.no), (dialog1, which) -> {
                //Dont do anything
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        });

        wrongnumber.setOnClickListener(v -> onBackPressed());

        sendVerificationCodeToUser(phoneNo);

    }


    private void sendVerificationCodeToUser(String phoneNo) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);        // OnVerificationStateChangedCallbacks

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }


            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Verification completed successfully
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Calendar calendar = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                                String timestamp = simpleDateFormat.format(calendar.getTime());

                                if(snapshot.exists()){
                                    startActivity(new Intent(VerifyOTP.this, Phone_setupActivity.class));
                                }else {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("id", userid);
                                    hashMap.put("phoneno",phoneNo);
                                    hashMap.put("imageURL", "default");
                                    hashMap.put("username", "");
                                    hashMap.put("DT", "");
                                    hashMap.put("typingto","");
                                    hashMap.put("joined_on", timestamp);
                                    reference.setValue(hashMap).addOnCompleteListener(task1 -> startActivity(new Intent(VerifyOTP.this, Phone_setupActivity.class)
                                    .putExtra("method","phone")));
                                }
                            }




                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(VerifyOTP.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    public void callNextScreenFromOTP(View view){
        String code = Objects.requireNonNull(pinFromUser.getText()).toString();
        if(!code.isEmpty()){
            verifyCode(code);
        }
    }
}