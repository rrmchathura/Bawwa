package com.example.bauwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if(currentUser == null)
                {
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                } else{
                    currentUserID = firebaseAuth.getCurrentUser().getUid();
                    userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    userDatabaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String userType = snapshot.child("userType").getValue().toString();

                                if(userType.equals("user")) {
                                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                } else if(userType.equals("admin")) {
                                    Intent adminIntent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                                    adminIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(adminIntent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        },1000);
    }
}