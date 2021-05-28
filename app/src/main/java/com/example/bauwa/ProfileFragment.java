package com.example.bauwa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private View view;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference, postDatabaseReference;

    private TextView textViewName, textViewEmail, textViewPhone;
    private Button msgBtn;
    private FloatingActionButton addPostBtn;

    private String currentUserID;

    private RecyclerView postList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        textViewName = view.findViewById(R.id.profile_fragment_username);
        textViewEmail = view.findViewById(R.id.profile_fragment_user_email);
        textViewPhone = view.findViewById(R.id.profile_fragment_user_phone);
        msgBtn = view.findViewById(R.id.profile_fragment_msg_btn);
        addPostBtn = view.findViewById(R.id.add_post_btn);

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatListActivity.class);
                startActivity(intent);
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostStepOneIntent = new Intent(getContext(), MainMenuActivity.class);
                startActivity(addPostStepOneIntent);
            }
        });

        postList = (RecyclerView) view.findViewById(R.id.profile_post_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        GetUserData();

        LoadPosts();

        return view;
    }

    private void LoadPosts() {

        Query query = postDatabaseReference.orderByChild("userID").equalTo(currentUserID);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        FirebaseRecyclerAdapter<PostModel, PostViewHolder> adapter = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull PostModel postModel) {

                postViewHolder.acceptBtn.setVisibility(View.GONE);
                postViewHolder.deleteBtn.setVisibility(View.GONE);

                String status = postModel.getStatus();
                if(status.equals("approved")) {
                    postViewHolder.postTimeNDate.setText(postModel.getPostDate()+" • "+postModel.getPostTime());

                    String contact = postModel.getContact();
                    if(!contact.isEmpty()) {
                        postViewHolder.postContact.setVisibility(View.VISIBLE);
                        postViewHolder.postContact.setText(contact);
                    }

                    String shop = postModel.getShop();
                    if(!shop.isEmpty()) {
                        postViewHolder.postShop.setVisibility(View.VISIBLE);
                        postViewHolder.postShop.setText(shop);
                    }

                    String openTime = postModel.getOpenTime();
                    if(!openTime.isEmpty()) {
                        postViewHolder.postTime.setVisibility(View.VISIBLE);
                        postViewHolder.postTime.setText(openTime);
                    }

                    postViewHolder.postDescription.setText(postModel.getPostDescription());
                    postViewHolder.postImage.setVisibility(View.GONE);

                    String imageUrl = postModel.getImage();
                    if(imageUrl != null) {
                        Picasso.with(getContext()).load(imageUrl).into(postViewHolder.postImage);
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

        TextView postTimeNDate, postDescription, postOwner, postShop, postContact, postTime;
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
            postShop =itemView.findViewById(R.id.post_shop);
            postContact = itemView.findViewById(R.id.post_contact);
            postTime = itemView.findViewById(R.id.post_time);
        }
    }

    private void GetUserData() {
        userDatabaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    textViewName.setText(snapshot.child("userName").getValue().toString());
                    textViewEmail.setText(snapshot.child("email").getValue().toString());
                    textViewPhone.setText(snapshot.child("phone").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}