package com.example.articlesapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.example.articlesapp.Models.Users;
import com.example.articlesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static maes.tech.intentanim.CustomIntent.customType;

public class SetupProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    CircleImageView imgProfile;
    EditText edtNameSetProfile, edtMobNoSetProfile;
    ProgressDialog dialog;
    AppCompatButton btnSetupProfile;
    String email;
    InputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

        imgProfile = findViewById(R.id.imageView);
        edtNameSetProfile = findViewById(R.id.edtNameSetProfile);
        btnSetupProfile = findViewById(R.id.btnSetupProfile);
        edtMobNoSetProfile = findViewById(R.id.edtMobNoSetProfile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
                customType(SetupProfileActivity.this, "fadein-to-fadeout");

            }
        });

        btnSetupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtNameSetProfile.getText().toString();
                email = getIntent().getStringExtra("email");
                String mobileNumber = edtMobNoSetProfile.getText().toString().trim();

                if (name.contains(" ")) {
                    edtNameSetProfile.setError("No Spaces Allowed");
                    edtNameSetProfile.requestFocus();
                    return;
                }

                if (name.isEmpty()) {
                    edtNameSetProfile.setError("Please enter username");
                    edtNameSetProfile.requestFocus();
                    return;
                }

                if (name.length() > 20) {
                    edtNameSetProfile.setError("Please enter 20 character username");
                    edtNameSetProfile.requestFocus();
                    return;
                }


                if (mobileNumber.isEmpty()) {
                    edtMobNoSetProfile.setError("Please enter 10 digit number");
                    edtMobNoSetProfile.requestFocus();
                    return;
                }

                if (mobileNumber.length() > 10) {
                    edtMobNoSetProfile.setError("Please enter 10 digit number");
                    edtMobNoSetProfile.requestFocus();
                    return;
                }

                if (mobileNumber.length() <= 9) {
                    edtMobNoSetProfile.setError("Please enter 10 digit number");
                    edtMobNoSetProfile.requestFocus();
                    return;
                }

                dialog = new ProgressDialog(SetupProfileActivity.this);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                dialog.show();

//                String emaili = getIntent().getStringExtra("Email");
//                String pass = getIntent().getStringExtra("Password");


                if (selectedImage != null) {
                    StorageReference reference = storage.getReference()
                            .child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage)
                            .addOnCompleteListener(new
                            OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                    String imageUrl = uri.toString();
                                    String uid = auth.getUid();
                                    String name = edtNameSetProfile.getText().toString();
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                       email = firebaseUser.getEmail();
                                    }
                                    Users user = new Users(uid, name, email, imageUrl, mobileNumber);

                                    database.getReference()
                                    .child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Intent intent = new Intent(SetupProfileActivity.this,
                                        MainActivity.class);
                                        startActivity(intent);
                                        customType(SetupProfileActivity.this,
                                        "fadein-to-fadeout");
                                        dialog.dismiss();
                                        finish();
                                    }
                                    });
                                    }
                                    });
                                  }
                             }

                          });
                } else {
/*//                    Uri uri2 = Uri.parse("android.resource://"+getApplicationContext()
//                            .getPackageName()+"/drawable/avatar");
                    InputStream stream = null;
                    try {
                        stream = getAssets().open("avatar.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Drawable d = Drawable.createFromStream(stream, null);
                    Uri uri2 = Uri.parse(String.valueOf(d));
//                    Uri uri2 = Uri.parse("android.resource://"+getApplicationContext()
//                            .getPackageName()+"/"+R.drawable.avatar);
                    StorageReference reference = storage.getReference()
                            .child("Profiles").child(auth.getUid());
                    reference.putFile(uri2)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String uid = auth.getUid();
//                    String phone = auth.getCurrentUser().getPhoneNumber();
                                                String nextUri = uri2.toString();
                                                Users user = new Users(uid, name, email,
                                                        nextUri, mobileNumber);
                                                Log.e("e",nextUri);
                                                database.getReference()
                                                        .child("users")
                                                        .child(uid)
                                                        .setValue(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent intent =
                                                                        new Intent(SetupProfileActivity.this,
                                                                                MainActivity.class);
                                                                startActivity(intent);
                                                                customType(SetupProfileActivity.this, "fadein-to-fadeout");
                                                                finish();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                }
                            });*/

                    /*String uid = auth.getUid();
//                    String phone = auth.getCurrentUser().getPhoneNumber();
                    Uri uri = Uri.parse("android.resource://com.example.articlesapp/drawable/avatar");
                    try {
                        stream = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Users user = new Users(uid, name, email,
                            stream.toString(), mobileNumber);
                    Log.e("e",uri.toString());
                    Log.e("e",stream.toString());*/

                    String uid = auth.getUid();
//                    String phone = auth.getCurrentUser().getPhoneNumber();

                    Users user = new Users(uid, name, email,
                            "No Image", mobileNumber);

                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent =
                                            new Intent(SetupProfileActivity.this,
                                                    MainActivity.class);
                                    startActivity(intent);
                                    customType(SetupProfileActivity.this,
                                            "fadein-to-fadeout");
                                    dialog.dismiss();
                                    finish();

                                }
                            });

                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {
                imgProfile.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.e("e","on destroyed");
//        Intent i = new Intent(SetupProfileActivity.this,
//                LoginActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        finish();
//    }
}