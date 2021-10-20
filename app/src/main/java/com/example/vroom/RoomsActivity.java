package com.example.vroom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RoomsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String username;
    private ArrayList<Room> room_list;
    private FloatingActionButton add_room;
    private RecyclerAdapterRooms adapter;
    private RecyclerView recyclerView;
    private String[] excludeIDs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooms_activity);

        Toolbar mainToolbar = findViewById(R.id.toolbar_rooms);
        setSupportActionBar(mainToolbar);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        Button my_rooms = findViewById(R.id.my_rooms);
        my_rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomsActivity.this, MyRoomsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        db = new DatabaseHelper(this);
        excludeIDs = db.getMyRoomsIDs(username);
        room_list = db.getRooms(username, excludeIDs);

        recyclerView = (RecyclerView) findViewById(R.id.room_list);
        adapter = new RecyclerAdapterRooms(room_list, this, username);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add_room = findViewById(R.id.fab);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewRoomDialog(username);
            }
        });
    }

    void showNewRoomDialog(String username){
        Dialog dialog = new Dialog(RoomsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_room_dialog);
        final EditText new_room_topic = dialog.findViewById(R.id.new_room_topic);
        final EditText new_room_password = dialog.findViewById(R.id.new_room_password);
        Button create_room = dialog.findViewById(R.id.new_room_button);
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Room room = new Room();
                room.setUsername(username);
                room.setTopic(new_room_topic.getText().toString());
                room.setPassword(new_room_password.getText().toString());
                db.createRoom(room);
                room = db.getRoomByUsername(username);
                db.addUnlockedRoom(username, room.getId());
                Intent intent = new Intent(RoomsActivity.this, MyRoomsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        excludeIDs = db.getMyRoomsIDs(username);
        room_list = db.getRooms(username, excludeIDs);

        recyclerView = (RecyclerView) findViewById(R.id.room_list);
        adapter = new RecyclerAdapterRooms(room_list, this, username);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
