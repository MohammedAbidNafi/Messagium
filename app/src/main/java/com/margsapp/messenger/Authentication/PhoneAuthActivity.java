package com.margsapp.messenger.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.margsapp.messenger.R;

import java.util.Objects;

public class PhoneAuthActivity extends AppCompatActivity {

    EditText phoneInputLayout;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        phoneInputLayout = findViewById(R.id.phone_number);
        countryCodePicker = findViewById(R.id.country_code_picker);
    }

    public void callVerifyOTP(View view){


        String PhoneNumber = Objects.requireNonNull(phoneInputLayout.getText().toString());


        if (PhoneNumber.charAt(0) == '0') {
            PhoneNumber = PhoneNumber.substring(1);
        }

        String _phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+PhoneNumber;


        Intent intent = new Intent(PhoneAuthActivity.this, VerifyOTP.class);
        intent.putExtra("phoneNo", _phoneNumber);
        startActivity(intent);
    }


}