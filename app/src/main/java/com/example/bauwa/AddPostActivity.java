package com.example.bauwa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class    AddPostActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference postDatabaseReference;

    private EditText editTextPostLocation, editTextPostShopName, editTextPostOpenTime, editTextPostContact, editTextPostDescription;
    private TextView selectDocumentBtn;
    private Button submitButton;
    private ImageView imageViewDocument;

    private Uri uriImage;

    private static final int Gallery_Pick = 1;

    private ProgressDialog loadingBar;

    private Toolbar toolbar;

    private String postType, currentUserID, postKey, imageUrl, locationLatitude="", locationLongitude="", currentLocation="";

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        postType = intent.getExtras().getString("postType");
        getSupportActionBar().setTitle(postType);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        postDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        editTextPostDescription = findViewById(R.id.add_post_description);
        editTextPostDescription.setHint("Important note about "+postType);
        editTextPostLocation = findViewById(R.id.add_post_location);
        editTextPostShopName = findViewById(R.id.add_post_shop);
        editTextPostOpenTime = findViewById(R.id.add_post_time);
        editTextPostContact = findViewById(R.id.add_post_contact);
        selectDocumentBtn = findViewById(R.id.add_post_select_document_btn);
        submitButton = findViewById(R.id.add_post_submit_btn);
        imageViewDocument = findViewById(R.id.add_post_document);

        if(postType.equals("Donate Food")) {
            editTextPostShopName.setVisibility(View.VISIBLE);
            editTextPostContact.setVisibility(View.VISIBLE);
        }

        if(postType.equals("Pet Clinic")) {
            editTextPostContact.setVisibility(View.VISIBLE);
            editTextPostOpenTime.setVisibility(View.VISIBLE);
        }

        selectDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAndSubmit();
            }
        });

        GetCurrentLocation();

    }

    private void GetCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null) {

                        try {
                            Geocoder geocoder = new Geocoder(AddPostActivity.this, Locale.getDefault());
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            locationLatitude = String.valueOf(addressList.get(0).getLatitude());
                            locationLongitude = String.valueOf(addressList.get(0).getLongitude());
                            currentLocation = String.valueOf(addressList.get(0).getLocality());

                            editTextPostLocation.setText(currentLocation);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void ValidateAndSubmit() {
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMdd");
        String date = currentDate.format(calendar1.getTime());

        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        String time = currentTime.format(calendar2.getTime());

        postKey = date + time;

        Boolean isValidate =  false;

        String description = editTextPostDescription.getText().toString();
        String shop = editTextPostShopName.getText().toString();
        String contact = editTextPostContact.getText().toString();
        String openTime = editTextPostOpenTime.getText().toString();

        if(postType.equals("Help Dog")) {
            if(description.isEmpty()) {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                isValidate = true;
            }
        }

        if(postType.equals("Donate Food")) {
            if(description.isEmpty() || shop.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                isValidate = true;
            }
        }

        if(postType.equals("Pet Clinic")) {
            if(description.isEmpty() || openTime.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                isValidate = true;
            }
        }

        if(isValidate) {
            loadingBar = new ProgressDialog(this);
            String ProgressDialogMessage="Submitting...";
            SpannableString spannableMessage=  new SpannableString(ProgressDialogMessage);
            spannableMessage.setSpan(new RelativeSizeSpan(1.3f), 0, spannableMessage.length(), 0);
            loadingBar.setMessage(spannableMessage);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setCancelable(false);

            if(uriImage != null) {
                StoreImageToStorage();
            } else {
                SavePostData();
            }
        }
    }

    private void SavePostData() {

        String description = ""; description = editTextPostDescription.getText().toString();
        String shop = ""; shop = editTextPostShopName.getText().toString();
        String contact = ""; contact = editTextPostContact.getText().toString();
        String openTime = ""; openTime = editTextPostOpenTime.getText().toString();

        Calendar calendar4 = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        String date = currentDate.format(calendar4.getTime());

        Calendar calendar3 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        String time = currentTime.format(calendar3.getTime());

        HashMap postMap = new HashMap();
        postMap.put("userID",currentUserID);
        postMap.put("postDate",date);
        postMap.put("postTime",time);
        postMap.put("postDescription",description);
        postMap.put("shop",shop);
        postMap.put("contact",contact);
        postMap.put("openTime",openTime);
        postMap.put("locationLatitude",locationLatitude);
        postMap.put("locationLongitude",locationLongitude);
        postMap.put("currentLocation",currentLocation);
        postMap.put("searchLocation",currentLocation.toLowerCase());
        postMap.put("status","pending");
        postMap.put("postType",postType);

        if(uriImage != null){
            postMap.put("image",imageUrl);
        }

        postDatabaseReference.child(postKey+currentUserID).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    loadingBar.dismiss();
                    Toast.makeText(AddPostActivity.this, "Post submitted. Wait for admin approve.", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(AddPostActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                else
                {
                    loadingBar.dismiss();
                    String msg = task.getException().getMessage();
                    Toast.makeText(AddPostActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void StoreImageToStorage() {

        final StorageReference filePath = storageReference.child("Post Images").child(uriImage.getLastPathSegment() + postKey + currentUserID + ".jpg");

        filePath.putFile(uriImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();
                            SavePostData();
                        }
                    });
                } else {
                    String msg = task.getException().getMessage();
                    Toast.makeText(AddPostActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            uriImage = data.getData();
            imageViewDocument.setImageURI(uriImage);
            selectDocumentBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}