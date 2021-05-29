package com.example.bauwa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textViewNewsFeedBtn, textViewPlaceBtn, textViewFoodsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewNewsFeedBtn = findViewById(R.id.main_menu_news_feed_btn);
        textViewNewsFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostIntent = new Intent(MainMenuActivity.this, AddPostActivity.class);
                addPostIntent.putExtra("postType", "Help Dog");
                startActivity(addPostIntent);
            }
        });


        textViewPlaceBtn = findViewById(R.id.main_menu_place_btn);
        textViewPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostIntent = new Intent(MainMenuActivity.this, AddPostActivity.class);
                addPostIntent.putExtra("postType", "Donate Food");
                startActivity(addPostIntent);
            }
        });


        textViewFoodsBtn = findViewById(R.id.main_menu_foods_btn);
        textViewFoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostIntent = new Intent(MainMenuActivity.this, AddPostActivity.class);
                addPostIntent.putExtra("postType", "Pet Clinic");
                startActivity(addPostIntent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}