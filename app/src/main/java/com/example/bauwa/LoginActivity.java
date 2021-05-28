package com.example.bauwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String currentUserID;
    private DatabaseReference userDatabaseReference;

    private EditText editTextEmail, editTextPassword;
    private Button loginBtn;
    private TextView textViewDoNotHaveAccBtn;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        textViewDoNotHaveAccBtn = findViewById(R.id.do_not_have_acc_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        textViewDoNotHaveAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mainIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAndLogin();
            }
        });
    }

    private void ValidateAndLogin() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar = new ProgressDialog(this);
            String ProgressDialogMessage="Logging...";
            SpannableString spannableMessage=  new SpannableString(ProgressDialogMessage);
            spannableMessage.setSpan(new RelativeSizeSpan(1.3f), 0, spannableMessage.length(), 0);
            loadingBar.setMessage(spannableMessage);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setCancelable(false);

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        currentUserID = firebaseAuth.getCurrentUser().getUid();
                        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                        userDatabaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    String userType = snapshot.child("userType").getValue().toString();

                                    if(userType.equals("user")) {
                                        loadingBar.dismiss();
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    } else if(userType.equals("admin")) {
                                        loadingBar.dismiss();
                                        Intent adminIntent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
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

                    } else {
                        loadingBar.dismiss();
                        String msg = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}