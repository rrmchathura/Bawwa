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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatListActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference, messagesDatabaseReference;

    private RecyclerView chatList;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat");

        firebaseAuth= FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        messagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);

        chatList = (RecyclerView) findViewById(R.id.chat_list);
        chatList.setNestedScrollingEnabled(false);
        chatList.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        /* define comment order*/
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(linearLayoutManager);

        RetrieveAllChats();
    }

    private void RetrieveAllChats()
    {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(messagesDatabaseReference, MessageModel.class)
                .build();

        FirebaseRecyclerAdapter<MessageModel, ChatViewHolder> adapter = new FirebaseRecyclerAdapter<MessageModel, ChatViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, int i, @NonNull MessageModel messageModel)
            {
                final String userID = getRef(i).getKey();
                if(!userID.isEmpty())
                {
                    userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                if(dataSnapshot.hasChild("userName"))
                                {
                                    String name = dataSnapshot.child("userName").getValue().toString();
                                    if(!name.isEmpty())
                                    {
                                        chatViewHolder.userName.setText(name);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });
                }

                chatViewHolder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent chatIntent = new Intent(ChatListActivity.this, ChatActivity.class);
                        chatIntent.putExtra("userID", userID);
                        startActivity(chatIntent);
                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user,parent,false);
                ChatViewHolder chatViewHolder = new ChatViewHolder(view);
                return chatViewHolder;
            }
        };

        chatList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;

        public ChatViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.row_user_username);
        }
    }


    /* toolbar back button click action */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}