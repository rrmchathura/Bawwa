package com.example.bauwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;

    private EditText editTextName, editTextPhone, editTextEmail, editTextPassword;
    private Button registerBtn;

    private String currentUserID;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.register_email);
        editTextPhone = findViewById(R.id.register_phone);
        editTextName = findViewById(R.id.register_name);
        editTextPassword = findViewById(R.id.register_password);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAndRegisterUser();
            }
        });
    }

    private void ValidateAndRegisterUser() {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields Can not be Empty", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar = new ProgressDialog(this);
            String ProgressDialogMessage="Creating Account...";
            SpannableString spannableMessage=  new SpannableString(ProgressDialogMessage);
            spannableMessage.setSpan(new RelativeSizeSpan(1.3f), 0, spannableMessage.length(), 0);
            loadingBar.setMessage(spannableMessage);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setCancelable(false);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        currentUserID = firebaseAuth.getCurrentUser().getUid();
                        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                        String joinedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

                        HashMap userMap = new HashMap();
                        userMap.put("userName", name);
                        userMap.put("phone", phone);
                        userMap.put("joinedDate", joinedDate);
                        userMap.put("email", email);
                        userMap.put("userType", "user");

                        userDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                } else{
                                    loadingBar.dismiss();
                                    String msg2 = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, msg2, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        loadingBar.dismiss();
                        String msg = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}