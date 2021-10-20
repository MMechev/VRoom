package com.example.vroom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyRoomsActivity extends AppCompatActivity {

    private ArrayList<Room> my_rooms;
    private String username;
    private DatabaseHelper db;
    private RecyclerAdapterRooms adapter;
    private FloatingActionButton add_room;
    private String[] roomIDS;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        setContentView(R.layout.my_rooms_activity);
        db = new DatabaseHelper(this);
        roomIDS = db.getMyRoomsIDs(username);
        my_rooms = db.getMyRooms(roomIDS);

        recyclerView = (RecyclerView) findViewById(R.id.my_room_list);
        adapter = new RecyclerAdapterRooms(my_rooms, this, username);
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
        Dialog dialog = new Dialog(MyRoomsActivity.this);
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
                room = db.getRoomByUsername(username);
                my_rooms.add(0, room);
                adapter.notifyAll();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
