package com.example.articlesapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.articlesapp.Models.Users;
import com.example.articlesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class EditProfileActivity extends AppCompatActivity {

    ImageView crossEditProfile, imageViewProfileFP;
    String mobileNumber, name;
    EditText edtMobEditProfile;
//    AppCompatButton btnUpdateProfile;
    ImageView btnUpdateProfile;
    TextView tvChangeImageEp,tvRemoveImageEp;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseUser user;
    String uid;
    Uri selectedImage;
    ProgressDialog dialog;
    String imageUrl;
    String mobilenumberNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        uid = auth.getUid();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mobileNumber = getIntent().getStringExtra("mobilenumber");
        name = getIntent().getStringExtra("name");
        tvChangeImageEp = findViewById(R.id.tvChangeImageEp);
        edtMobEditProfile = findViewById(R.id.edtMobEditProfile);
        imageViewProfileFP = findViewById(R.id.imageViewProfileFP);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        tvRemoveImageEp = findViewById(R.id.tvRemoveImageEp);
        edtMobEditProfile.setText(mobileNumber);

        Glide.with(this)
                .load(getIntent().getStringExtra("userimage"))
                .placeholder(R.drawable.avatar)
                .into(imageViewProfileFP);

        dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        crossEditProfile = findViewById(R.id.crossEditProfile);
        crossEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvChangeImageEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
                customType(EditProfileActivity.this, "fadein-to-fadeout");
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String name = edtUsernameEditProfile.getText().toString().trim();
//                String email = edtEmailEditProfile.getText().toString().trim();
                mobilenumberNew = edtMobEditProfile.getText().toString().trim();

                if (mobilenumberNew.isEmpty()) {
                    edtMobEditProfile.setError(getString(R.string.please_enter_10_digit_number));
                    edtMobEditProfile.requestFocus();
                    return;
                }

                if (mobilenumberNew.length() > 10) {
                    edtMobEditProfile.setError(getString(R.string.please_enter_10_digit_number));
                    edtMobEditProfile.requestFocus();
                    return;
                }

                if (mobilenumberNew.length() <= 9) {
                    edtMobEditProfile.setError(getString(R.string.please_enter_10_digit_number));
                    edtMobEditProfile.requestFocus();
                    return;
                }

                dialog.show();

//                Uri uri = Uri.parse(String.valueOf(selectedImage));

//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setDisplayName(name)
//                        .setPhotoUri(Uri.parse(String.valueOf(selectedImage)))
//                        .build();
//
//                if (user != null) {
//                    user.updateProfile(profileUpdates)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d("e", "User profile updated.");
//                                    }
//                                }
//                            });
//                }



//                database.getReference()
//                        .child("users")
//                        .child(uid)
//                        .child("profileImage")
//                        .setValue(selectedImage);
               /* //mob num. should also be updated in authentication
                if (mobilenumberNew!=null){
                    database.getReference()
                            .child("users")
                            .child(uid)
                            .child("mobileNumber")
                            .setValue(mobilenumberNew)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent =
                                    new Intent(EditProfileActivity.this,
                                            MainActivity.class);
                            startActivity(intent);
                            customType(EditProfileActivity.this, "fadein-to-fadeout");
                            dialog.dismiss();
                            finish();
                        }
                    });

                }else {
                    Toast.makeText(EditProfileActivity.this,
                            "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }*/


                if (selectedImage != null) {
                    StorageReference reference = storage.getReference()
                            .child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageUrl = uri.toString();
                                                database.getReference()
                                                        .child("users")
                                                        .child(uid)
                                                        .child("profileImage")
                                                        .setValue(imageUrl);
                                                database.getReference()
                                                        .child("users")
                                                        .child(uid)
                                                        .child("mobileNumber")
                                                        .setValue(mobilenumberNew)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent intent =
                                                                        new Intent(EditProfileActivity.this,
                                                                                MainActivity.class);
                                                                startActivity(intent);
                                                                customType(EditProfileActivity.this, "fadein-to-fadeout");
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        });
                                                Log.e("e", mobilenumberNew + "-" + uid);
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });

                } else {

//                    Toast t = Toast.makeText(EditProfileActivity.this,
//                            R.string.mobile_num_updated,
//                            Toast.LENGTH_LONG );
//                    t.setGravity(Gravity.CENTER,0,0);
//                    t.show();
                    /*String email = getIntent().getStringExtra("email");
                    Log.e("e",email);
                    Users user = new Users(uid, name, email,
                            "No Image", mobileNumber);
                    Log.e("e",uid+" "+email+" "+name+" "+mobilenumber);
                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent =
                                            new Intent(EditProfileActivity.this,
                                                    MainActivity.class);
                                    startActivity(intent);
                                    customType(EditProfileActivity.this, "fadein-to-fadeout");
                                    finish();
                                }
                            });*/

                    //mob num. should also be updated in authentication
                    if (mobilenumberNew!=null){
                        database.getReference()
                                .child("users")
                                .child(uid)
                                .child("mobileNumber")
                                .setValue(mobilenumberNew)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent =
                                                new Intent(EditProfileActivity.this,
                                                        MainActivity.class);
                                        startActivity(intent);
                                        customType(EditProfileActivity.this,
                                                "fadein-to-fadeout");
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                    }else {
                        Toast to = Toast.makeText(EditProfileActivity.this,
                                R.string.something_went_wrong,
                                Toast.LENGTH_LONG );
                        to.setGravity(Gravity.CENTER,0,0);
                        to.show();
//                        Toast.makeText(EditProfileActivity.this,
//                                "Something went wrong!",
//                                Toast.LENGTH_SHORT).show();
                    }

                    /*database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent =
                                            new Intent(EditProfileActivity.this,
                                                    MainActivity.class);
                                    startActivity(intent);
                                    customType(EditProfileActivity.this, "fadein-to-fadeout");
                                    dialog.dismiss();
                                    finish();

                                }
                            });*/
                }


            }
        });

        tvRemoveImageEp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if (selectedImage!=null){
//                    imageViewProfileFP.setImageURI(selectedImage);
//                }else{
//
//                }

                dialog.show();

                String email = getIntent().getStringExtra("email");
                Log.e("e",email);
                Users user = new Users(uid, name, email,
                        "No Image", mobileNumber);
                Log.e("e",uid+" "+email+" "+name+" "+mobilenumberNew);
                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent =
                                        new Intent(EditProfileActivity.this,
                                                MainActivity.class);
                                startActivity(intent);
                                customType(EditProfileActivity.this, "fadein-to-fadeout");
                                dialog.dismiss();
                                finish();
                            }
                        });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (data.getData() != null) {

//                imageViewProfileFP.setImageURI(data.getData());
//                selectedImage = data.getData();

                selectedImage = data.getData();

//                imageViewProfileFP.setImageURI(selectedImage);

                Glide.with(EditProfileActivity.this)
                        .load(selectedImage)
                        .placeholder(R.drawable.avatar)
                        .into(imageViewProfileFP);

            }
        }

    }

}