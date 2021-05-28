package com.example.bauwa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private ImageView logoutBtn;

    private DatabaseReference postDatabaseReference, deletePostDatabaseReference, userDatabaseReference;
    private RecyclerView postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bauwa - Admin");

        firebaseAuth = FirebaseAuth.getInstance();
        postDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        postList = (RecyclerView) findViewById(R.id.admin_post_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        LoadPosts();

        logoutBtn = findViewById(R.id.logout_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent loginIntent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        });

    }

    private void LoadPosts() {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(postDatabaseReference, PostModel.class)
                .build();

        FirebaseRecyclerAdapter<PostModel, PostViewHolder> adapter = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull PostModel postModel) {

                String postID = getRef(i).getKey();

                String status = postModel.getStatus();

                if(status.equals("pending")) {
                    postViewHolder.postTimeNDate.setText(postModel.getPostDate()+" • "+postModel.getPostTime());
                    postViewHolder.postDescription.setText(postModel.getPostDescription());
                    postViewHolder.postImage.setVisibility(View.GONE);

                    String imageUrl = postModel.getImage();
                    if(imageUrl != null) {
                        Picasso.with(AdminDashboardActivity.this).load(imageUrl).into(postViewHolder.postImage);
                        postViewHolder.postImage.setVisibility(View.VISIBLE);
                    }

                    String userID = postModel.getUserID();
                    userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                postViewHolder.postOwner.setText(snapshot.child("userName").getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    postViewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HashMap postMap = new HashMap();
                            postMap.put("status", "approved");

                            postDatabaseReference.child(postID).updateChildren(postMap);
                        }
                    });

                    postViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletePostDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID);
                            deletePostDatabaseReference.removeValue();
                        }
                    });
                } else {
                    postViewHolder.itemView.setVisibility(View.GONE);
                    postViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);
                PostViewHolder postViewHolder = new PostViewHolder(view);
                return postViewHolder;
            }
        };

        postList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTimeNDate, postDescription, postOwner;
        ImageView postImage;
        Button acceptBtn, deleteBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postOwner =itemView.findViewById(R.id.post_owner);
            postTimeNDate =itemView.findViewById(R.id.post_date_n_time);
            postDescription =itemView.findViewById(R.id.post_description);
            postImage =itemView.findViewById(R.id.post_document);
            acceptBtn = itemView.findViewById(R.id.post_accept_btn);
            deleteBtn = itemView.findViewById(R.id.post_delete_btn);
        }
    }
}