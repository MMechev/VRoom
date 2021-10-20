package com.example.vroom;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private String username;
    private int roomID;
    private DatabaseHelper db;
    private ArrayList<Message> messages;
    private RecyclerAdapterMessages adapter;
    private RecyclerView recyclerView;
    private EditText inputMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        roomID = intent.getIntExtra("room_id", -1);
        db = new DatabaseHelper(this);
        Toolbar mainToolbar = findViewById(R.id.toolbar_rooms);
        mainToolbar.setTitle(db.getRoomTopic(roomID));
        setSupportActionBar(mainToolbar);

        messages = db.getMessages(roomID);

        Button my_rooms = findViewById(R.id.my_rooms);
        my_rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, MyRoomsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        inputMsg = findViewById(R.id.new_message);
        Button addMsg = findViewById(R.id.new_message_button);

        addMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.setUsername(username);
                String pattern = "MM-dd-yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());
                msg.setDate(date);
                msg.setMessage(inputMsg.getText().toString());
                inputMsg.setText("");
                inputMsg.clearFocus();
                msg.setRoomID(roomID);
                db.createMessage(msg);
                messages.clear();
                ArrayList<Message> new_list = db.getMessages(roomID);
                messages.addAll(new_list);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.messages_recyclerview);
        adapter = new RecyclerAdapterMessages(messages, this, username);
        recyclerView.setAdapter(adapter);

        if(messages.size() > 0){
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if(messages.size() > 0){
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                    }
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }
}
