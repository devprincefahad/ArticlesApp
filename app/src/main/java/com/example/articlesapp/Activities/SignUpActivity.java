package com.example.articlesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class SignUpActivity extends AppCompatActivity {

    EditText edtNameSignUp, edtEmailSignUp, edtPasswordSignUp;
    AppCompatButton btnSignUp;
    TextView tvLogin;
    private ProgressDialog dialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

//    FireStore connection
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLang();

        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            customType(SignUpActivity.this, "fadein-to-fadeout");
            finish();
        }

        setContentView(R.layout.activity_sign_up);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

//        edtNameSignUp = findViewById(R.id.edtNameSignUp);
        edtEmailSignUp = findViewById(R.id.edtEmailSignUp);
        edtPasswordSignUp = findViewById(R.id.edtPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                customType(SignUpActivity.this, "fadein-to-fadeout");
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String username = edtNameSignUp.getText().toString().trim();
                String email = edtEmailSignUp.getText().toString().trim();
                String password = edtPasswordSignUp.getText().toString().trim();


                if (email.isEmpty()) {
                    edtEmailSignUp.setError("Please enter email!");
                    edtEmailSignUp.requestFocus();
                    return;
                }

                if (email.length() > 35) {
                    edtNameSignUp.setError("Email should be less than 35 characters");
                    edtNameSignUp.requestFocus();
                    return;
                }

                if (email.contains(" ")) {
                    edtEmailSignUp.setError("No Spaces Allowed");
                    edtEmailSignUp.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    edtPasswordSignUp.setError("Please enter a password!");
                    edtPasswordSignUp.requestFocus();
                    return;
                }

                if (password.contains(" ")) {
                    edtPasswordSignUp.setError("No Spaces Allowed");
                    edtPasswordSignUp.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmailSignUp.setError("Enter a valid Email address!");
                    edtEmailSignUp.requestFocus();
                    return;
                }

                if (password.length() < 6 && password.length() > 11) {
                    edtPasswordSignUp.setError("password should be of 6 characters!");
                    edtPasswordSignUp.requestFocus();
                    return;
                }

                dialog = new ProgressDialog(SignUpActivity.this);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                dialog.show();

                //create user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

//                              TO CHECK IF EMAIL ALREADY EXISTS
//                                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
//                                        Log.d("error",""+task.getResult().getSignInMethods().size());
//                                        if (task.getResult().getSignInMethods().size() == 0){
//                                            // email not existed
//                                            Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                                        }else {
//                                            // email existed
//                                            edtEmailSignUp.setError("Email already exists");
//                                        }
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });

//                                Toast.makeText(SignUpActivity.this,
//                                        "createUserWithEmail:onComplete:" +
//                                                task.isSuccessful(),
//                                        Toast.LENGTH_LONG).show();

                                if (task.isSuccessful()){
                                    Toast t = Toast.makeText(SignUpActivity.this, "Account Created Successfully",
                                            Toast.LENGTH_LONG );
                                    t.setGravity(Gravity.CENTER,0,0);
                                    t.show();
                                    dialog.dismiss();

                                }

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast t = Toast.makeText(SignUpActivity.this,
                                            "ERROR: " + task.getException(),
                                            Toast.LENGTH_LONG);
                                    t.setGravity(Gravity.CENTER,0,0);
                                    t.show();

                                    dialog.dismiss();
                                } else {
                                    Intent intent = new Intent(SignUpActivity.this,
                                            SetupProfileActivity.class);
                                    intent.putExtra("email", email);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    dialog.dismiss();
                                    customType(SignUpActivity.this,
                                            "fadein-to-fadeout");
                                    SignUpActivity.this.finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != dialog) {
            dialog.dismiss();
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