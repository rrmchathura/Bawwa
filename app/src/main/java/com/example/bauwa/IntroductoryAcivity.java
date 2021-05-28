package com.example.bauwa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class IntroductoryAcivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    TextView textView;

    SharedPreferences onBordingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introductory_acivity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBordingScreen = getSharedPreferences("onBordingScreen",MODE_PRIVATE);
                boolean isFirstTime = onBordingScreen.getBoolean("firstTime",true);

                if (isFirstTime){

                    SharedPreferences.Editor editor = onBordingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                    startActivity(intent);
                    finish();


                }
                else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }



            }

        },8000);


    }
    }
