package com.example.articlesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.articlesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import static maes.tech.intentanim.CustomIntent.customType;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btnLogin;
    EditText edtEmailLogin, edtPasswordLogin;
    TextView signUpTv, tvForgotPassword, changeLangLogin;
    ProgressDialog dialog;
    private static final String TAG = "LoginActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    //    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference collectionReference = db.collection("Users");
    private DatabaseReference userRef;

    private void showLanguageChangeDialog() {
        //array to display list of items in alert dialog
        final String[] listItems = {"English", "हिन्दी"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    setLocale("en");
                    recreate();

                } else if (i == 1) {
                    setLocale("hi");
                    recreate();

                }

                //dismiss when selected
                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        //show alert dialog
        alertDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLang();

        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            if (firebaseAuth.getCurrentUser() != null)
                isProfileSetupComplete(firebaseAuth);
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            customType(LoginActivity.this,"fadein-to-fadeout");
//            finish();
        }

        if (firebaseAuth.getCurrentUser() != null)
            isProfileSetupComplete(firebaseAuth);

        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();

        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtPasswordLogin = findViewById(R.id.edtPasswordLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signUpTv = findViewById(R.id.tvSignUp);
        changeLangLogin = findViewById(R.id.changeLangLogin);

        changeLangLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageChangeDialog();
            }
        });


        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                customType(LoginActivity.this, "fadein-to-fadeout");
            }
        });

        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                customType(LoginActivity.this, "fadein-to-fadeout");
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmailLogin.getText().toString().trim();
                String password = edtPasswordLogin.getText().toString().trim();


                if (email.isEmpty()) {
                    edtEmailLogin.setError("Please enter email!");
                    edtEmailLogin.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    edtPasswordLogin.setError("Please enter a password!");
                    edtPasswordLogin.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmailLogin.setError("Enter a valid Email address!");
                    edtEmailLogin.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    edtPasswordLogin.setError("Password should be of 6 characters!");
                    edtPasswordLogin.requestFocus();
                    return;
                }

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                dialog.show();

                //authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        dialog.dismiss();
                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            if (password.length() < 6) {
                                                edtPasswordLogin.setError("Password should be of 6 characters!");
                                            } else {

                                                Toast t = Toast.makeText(LoginActivity.this,
                                                        "Authentication Failed", Toast.LENGTH_SHORT);
                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                t.show();

                                            }
                                        } else {
                                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    customType(LoginActivity.this,"fadein-to-fadeout");
                                    LoginActivity.this.finish();*/

                                            // Check if user profile setup is complete
                                            if (firebaseAuth.getCurrentUser() != null)
                                                dialog.dismiss();
                                                isProfileSetupComplete(firebaseAuth);
                                        }
                                    }
                                });
            }
        });

        //yo moved
    }

    private void isProfileSetupComplete(FirebaseAuth firebaseAuth) {
        userRef = FirebaseDatabase.getInstance().getReference("users");
        String uid = firebaseAuth.getCurrentUser().getUid();
        if (firebaseAuth.getCurrentUser() != null) {
            userRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // check phone is null or not
                    String phone = snapshot.child("mobileNumber").getValue(String.class);
                    if (phone != null) {
                        // intent to main activity

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        customType(LoginActivity.this, "fadein-to-fadeout");
                        LoginActivity.this.finish();
                    } else {
                        // intent to setup activity


                        Intent intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                        customType(LoginActivity.this, "fadein-to-fadeout");
                        LoginActivity.this.finish();
                    }
                    Log.d(TAG, "onDataChange: " + phone);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext()
                .getResources()
                .updateConfiguration(config, getBaseContext()
                        .getResources().getDisplayMetrics());

        //save data to shared preferences

        SharedPreferences.Editor editor = getSharedPreferences("Settings", Activity.MODE_PRIVATE).edit();
        editor.putString("My_Lang", language);
        editor.apply();
    }

    public void loadLang() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "");
        setLocale(lang);
    }

}