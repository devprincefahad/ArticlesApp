package com.example.articlesapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.articlesapp.R;

import java.util.Locale;
import java.util.Objects;

import static maes.tech.intentanim.CustomIntent.customType;

public class ShowDataRVActivity extends AppCompatActivity {

    ImageView imgBackIcon;
    ImageView showArticleImage;
    TextView showArticleHeadline,showArticleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLang();
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_show_data_r_v);

        imgBackIcon = findViewById(R.id.imgBackIcon);
        showArticleImage = findViewById(R.id.showArticleImage);
        showArticleData = findViewById(R.id.showArticleData);
        showArticleHeadline = findViewById(R.id.showArticleHeadline);

        imgBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customType(ShowDataRVActivity.this, "fadein-to-fadeout");
                finish();
            }
        });

//      showArticleImage.setImageResource(getIntent().getIntExtra("articleImage",0));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            showArticleData.setText(Html.fromHtml(getIntent().getStringExtra("articleData"),
                    Html.FROM_HTML_MODE_COMPACT));

        } else {

            showArticleData.setText(Html.fromHtml(getIntent().getStringExtra("articleData")));

        }

        showArticleHeadline.setText(getIntent().getStringExtra("articleHeadline"));
//        showArticleData.setText(getIntent().getStringExtra("articleData"));

        Glide.with(this)
                .load(getIntent().getStringExtra("articleImage"))
                .placeholder(R.color.gray2)
                .into(showArticleImage);

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