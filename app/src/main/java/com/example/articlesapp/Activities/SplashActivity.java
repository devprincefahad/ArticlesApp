package com.example.articlesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.articlesapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            isProfileSetupComplete(firebaseAuth);
        }
        else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    customType(SplashActivity.this,"fadein-to-fadeout");
                    finish();
                }
            }, 4500);

        }

    }

    private void isProfileSetupComplete(FirebaseAuth firebaseAuth) {
        userRef = FirebaseDatabase.getInstance().getReference("users");
        String uid = firebaseAuth.getCurrentUser().getUid();
        if (firebaseAuth.getCurrentUser() != null) {
            userRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // check phone is null or not
                    String phone = snapshot.child("mobileNumber").getValue(String.class);
                    if (phone != null) {
                        // intent to main activity

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        customType(SplashActivity.this, "fadein-to-fadeout");
                        SplashActivity.this.finish();
                    } else {
                        // intent to setup activity


                        Intent intent = new Intent(SplashActivity.this, SetupProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                        customType(SplashActivity.this, "fadein-to-fadeout");
                        SplashActivity.this.finish();
                    }
                    Log.d("e", "onDataChange: " + phone);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }

    }
}