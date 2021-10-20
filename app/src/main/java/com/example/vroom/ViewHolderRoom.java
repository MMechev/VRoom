package com.example.vroom;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderRoom extends RecyclerView.ViewHolder {

    public TextView room_topic;
    public ImageView room_img;
    public TextView room_username;
    public ImageView door_key;
    public Button leave_room;

    public ViewHolderRoom(@NonNull View itemView) {
        super(itemView);
        room_topic = (TextView) itemView.findViewById(R.id.room_topic);
        room_img = (ImageView) itemView.findViewById(R.id.room_img);
        room_username = (TextView) itemView.findViewById(R.id.room_username);
        door_key = (ImageView) itemView.findViewById(R.id.door_key);
        leave_room = (Button) itemView.findViewById(R.id.leave_room_button);
    }
}
