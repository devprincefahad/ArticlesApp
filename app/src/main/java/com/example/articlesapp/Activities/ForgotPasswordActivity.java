package com.example.articlesapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.articlesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView tvSignUpFP, tvLoginFP;
    EditText edtEmailForgotPassword;
    AppCompatButton btnResetPassword;
    private FirebaseAuth auth;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
        }

        tvLoginFP = findViewById(R.id.tvLoginFP);
        tvSignUpFP = findViewById(R.id.tvSignUpFP);
        edtEmailForgotPassword = findViewById(R.id.edtEmailForgotPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        tvSignUpFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this,SignUpActivity.class));
                customType(ForgotPasswordActivity.this,"fadein-to-fadeout");
            }
        });

        tvLoginFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                customType(ForgotPasswordActivity.this,"fadein-to-fadeout");
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmailForgotPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    edtEmailForgotPassword.setError("Please enter email!");
                    edtEmailForgotPassword.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmailForgotPassword.setError("Enter a valid Email address!");
                    edtEmailForgotPassword.requestFocus();
                    return;
                }


                dialog = new ProgressDialog(ForgotPasswordActivity.this);
                dialog.setMessage("Sending reset instructions...");
                dialog.setCancelable(false);
                dialog.show();


                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast t = Toast.makeText(ForgotPasswordActivity.this,
                                            "We have sent you instructions to reset your password!",
                                            Toast.LENGTH_SHORT );
                                    t.setGravity(Gravity.CENTER,0,0);
                                    t.show();

                                } else {
                                    Toast t = Toast.makeText(ForgotPasswordActivity.this,
                                            "Failed to send reset email!",
                                            Toast.LENGTH_SHORT );
                                    t.setGravity(Gravity.CENTER,0,0);
                                    t.show();
                                }

                                dialog.dismiss();
                            }
                        });

            }
        });

    }
}