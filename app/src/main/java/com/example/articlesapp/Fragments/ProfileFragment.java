package com.example.articlesapp.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.articlesapp.Activities.EditProfileActivity;
import com.example.articlesapp.Activities.ForgotPasswordActivity;
import com.example.articlesapp.Activities.LoginActivity;
import com.example.articlesapp.Activities.MainActivity;
import com.example.articlesapp.Activities.SignUpActivity;
import com.example.articlesapp.Models.Users;
import com.example.articlesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static maes.tech.intentanim.CustomIntent.customType;

public class ProfileFragment extends Fragment {

    ImageView btnLogOutFP;
    CircleImageView imgProfileFP;
    TextView tvUsernameFP, tvEmailFP, tvMobNumFP,
            tvLanguageFP, tvYourProfile, tvEditProfileFP, tvChangePassProfileFP;
    ProgressBar progressProfile;
    private FirebaseAuth auth;
    ProgressDialog dialog;
    String name, email, mobileNumber;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    Users userProfile;

    public ProfileFragment() {
    }

    private void showLanguageChangeDialog() {
        //array to display list of items in alert dialog
        final String[] listItems = {"English", "हिन्दी"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    setLocale("en");
//                    getActivity().recreate();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    customType(getContext(), "fadein-to-fadeout");
                    getActivity().finish();

                } else if (i == 1) {
                    setLocale("hi");
//                    getActivity().recreate();
                    Intent intent2 = new Intent(getContext(), MainActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent2);
                    customType(getContext(), "fadein-to-fadeout");
                    getActivity().finish();


                }

                //dismiss when selected
                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        //show alert dialog
        alertDialog.show();

//        Intent i = new Intent(getContext(),HomeFragment.class);
//        startActivity(i);
//        getActivity().finish();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext()
                .getResources()
                .updateConfiguration(config, getActivity().getBaseContext()
                        .getResources().getDisplayMetrics());

        //save data to shared preferences

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", language);
        editor.apply();
    }

    //load language saved in shared prefs

    public void loadLang() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "");
        setLocale(lang);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadLang();

        View mRoot = inflater.inflate(R.layout.fragment_profile, container, false);

        setFadeAnimation(mRoot);
        auth = FirebaseAuth.getInstance();

        tvLanguageFP = mRoot.findViewById(R.id.tvLanguageFP);
        tvEditProfileFP = mRoot.findViewById(R.id.tvEditProfileFP);
        tvChangePassProfileFP = mRoot.findViewById(R.id.tvChangePassProfileFP);


        tvLanguageFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageChangeDialog();
            }
        });

        btnLogOutFP = mRoot.findViewById(R.id.btnLogOutFP);

        btnLogOutFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setMessage(getResources().getString(R.string.logout_sure))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                customType(getContext(), "fadein-to-fadeout");

                                Objects.requireNonNull(getActivity()).finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userId = user.getUid();

        imgProfileFP = mRoot.findViewById(R.id.imageViewProfileFP);

        tvEmailFP = mRoot.findViewById(R.id.tvEmailFP);
        tvUsernameFP = mRoot.findViewById(R.id.tvUsernameFP);
        tvMobNumFP = mRoot.findViewById(R.id.tvMobNumFP);

        progressProfile = mRoot.findViewById(R.id.progressProfile);

        tvYourProfile = mRoot.findViewById(R.id.tvYourProfile);

        progressProfile.setVisibility(View.VISIBLE);

        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (isAdded()) {
                    userProfile = snapshot.getValue(Users.class);
                    if (userProfile != null) {

                        name = userProfile.getName().trim();
                        email = userProfile.getEmail().trim();
                        mobileNumber = userProfile.getmobileNumber().trim();

                        if (userProfile.getProfileImage().equals("default")) {
                            imgProfileFP.setImageResource(R.drawable.avatar);
                        } else {
                            Glide.with(Objects.requireNonNull(getContext()))
                                    .load(userProfile.getProfileImage())
                                    .placeholder(R.drawable.avatar)
                                    .into(imgProfileFP);

                        }

                        progressProfile.setVisibility(View.GONE);
                        tvUsernameFP.setText(name);
                        tvEmailFP.setText(email);
                        tvMobNumFP.setText(mobileNumber);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(),
//                        "something went wrong",
//                        Toast.LENGTH_SHORT).show();
                Log.e("e", error.getMessage());
            }
        });

        tvEditProfileFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditProfileActivity.class);
                i.putExtra("name", userProfile.getName());
                i.putExtra("email", userProfile.getEmail());
                i.putExtra("mobilenumber", userProfile.getmobileNumber());
                i.putExtra("userimage", userProfile.getProfileImage());
                startActivity(i);
                customType(getContext(), "fadein-to-fadeout");
            }
        });

        tvChangePassProfileFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        //set title
                        .setTitle("Change Password")
                        //set message
                        .setMessage("Password reset link will be send to your linked email")
                        //set positive button
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what would happen when positive button is clicked
                                dialog = new ProgressDialog(getContext());
                                dialog.setMessage("Sending reset link...");
                                dialog.setCancelable(false);
                                dialog.show();
                                if (userProfile!=null){
                                    email = userProfile.getEmail();
                                }

                                auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast t = Toast.makeText(getContext(),
                                                            "We have sent you instructions to reset your password!",
                                                            Toast.LENGTH_SHORT);
                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                    t.show();

                                                } else {
                                                    Log.e("e",task.getException().getLocalizedMessage());
                                                    Log.e("e",task.toString());
                                                    Toast t = Toast.makeText(getContext(),
                                                            "Failed to send reset link!",
                                                            Toast.LENGTH_SHORT);
                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                    t.show();
                                                }
                                                dialog.dismiss();

                                            }
                                        });
                            }
                        })
                        //set negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();


            }
        });

        return mRoot;

    }


    private void setFadeAnimation(View itemView) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f); //(0.0f, 1.0f);
        anim.setDuration(400);
        itemView.startAnimation(anim);

    }

//    @Override
//    public void onDestroyView(){
//        super.onDestroyView();
//        reference.removeEventListener();
//
//    }

}

