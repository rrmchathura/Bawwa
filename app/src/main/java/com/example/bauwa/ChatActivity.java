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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageDatabaseReference;

    private Toolbar toolbar;

    private EditText editTextMsgInput;
    private ImageButton msgSendBtn;
    private String otherUserID, currentUserID;
    private RecyclerView msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat");

        editTextMsgInput = findViewById(R.id.msg_input);
        msgSendBtn = findViewById(R.id.msg_send_btn);

        Intent intent = getIntent();
        otherUserID = intent.getExtras().getString("userID");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        messageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages");

        msgList = (RecyclerView) findViewById(R.id.messages_list);
        msgList.setNestedScrollingEnabled(false);
        msgList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        msgList.setLayoutManager(linearLayoutManager);

        msgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        LoadMessages();
    }

    private void LoadMessages() {
        Query query = messageDatabaseReference.child(currentUserID).child(otherUserID);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class)
                .build();

        FirebaseRecyclerAdapter<MessageModel, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull MessageModel messages)
            {
                messageViewHolder.receiverSide.setVisibility(View.GONE);
                messageViewHolder.senderSide.setVisibility(View.GONE);

                String receiver = messages.getReceiver();
                String sender = messages.getSender();

                if(sender.equals(currentUserID))
                {
                    messageViewHolder.senderSide.setVisibility(View.VISIBLE);
                    messageViewHolder.senderMsg.setText(messages.getMsg());
                }

                if(receiver.equals(currentUserID))
                {
                    messageViewHolder.receiverSide.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMsg.setText(messages.getMsg());
                }
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_msg,parent,false);
                MessageViewHolder messageViewHolder = new MessageViewHolder(view);
                return messageViewHolder;
            }
        };

        msgList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView receiverMsg, senderMsg;
        LinearLayout receiverSide, senderSide;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.receiver_message);
            receiverSide = itemView.findViewById(R.id.message_receiver_side);
            senderMsg = itemView.findViewById(R.id.sender_message);
            senderSide = itemView.findViewById(R.id.message_sender_side);
        }
    }

    private void SendMessage() {
        String msg = editTextMsgInput.getText().toString();

        if(!msg.isEmpty()) {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("yyyyMMdd");
            String date = currentDate.format(calendar1.getTime());

            Calendar calendar2 = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
            String time = currentTime.format(calendar2.getTime());

            String msgKey = date + time;

            HashMap msg1Map = new HashMap();
            msg1Map.put("receiver",otherUserID);
            msg1Map.put("sender",currentUserID);
            msg1Map.put("msg",msg);
            messageDatabaseReference.child(currentUserID).child(otherUserID).child(msgKey).updateChildren(msg1Map);

            HashMap msg2Map = new HashMap();
            msg2Map.put("receiver",otherUserID);
            msg2Map.put("sender",currentUserID);
            msg2Map.put("msg",msg);
            messageDatabaseReference.child(otherUserID).child(currentUserID).child(msgKey).updateChildren(msg2Map);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(false);
            linearLayoutManager.setStackFromEnd(true);
            msgList.setLayoutManager(linearLayoutManager);

            editTextMsgInput.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}