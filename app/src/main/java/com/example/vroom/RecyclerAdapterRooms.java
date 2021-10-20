package com.example.vroom;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class RecyclerAdapterRooms extends RecyclerView.Adapter<ViewHolderRoom> {

    private ArrayList<Room> room_list;
    private Activity context;
    private String username;
    private DatabaseHelper db;

    public RecyclerAdapterRooms(ArrayList<Room> room_list, Activity application, String username){
        this.room_list = room_list;
        this.context = application;
        this.username = username;
        db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room, parent, false);
        ViewHolderRoom holder = new ViewHolderRoom(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRoom holder, int position) {
        holder.room_topic.setText(room_list.get(position).getTopic());
        holder.room_username.setText("u/" + room_list.get(position).getUsername());
        if(room_list.get(position).getPassword().contentEquals("")){
            holder.door_key.setImageResource(R.drawable.no_key);
        } else holder.door_key.setImageResource(R.drawable.door_key);
        Drawable lR = ContextCompat.getDrawable(context, R.drawable.exit_room);
        Drawable dR = ContextCompat.getDrawable(context, R.drawable.delete_room);
        if(username.contentEquals(room_list.get(position).getUsername())){
//            holder.leave_room.setCompoundDrawables(null, null, draw, null);
            holder.leave_room.setCompoundDrawablesWithIntrinsicBounds(null, null, dR, null);
        } else holder.leave_room.setCompoundDrawablesWithIntrinsicBounds(null, null, lR, null);
        holder.leave_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.removeUnlockedRoom(username, room_list.get(position).getId());
                if(username.contentEquals(room_list.get(position).getUsername())){
                    db.deleteRoom(room_list.get(position).getId());
                }
                room_list.remove(room_list.get(position));
                notifyDataSetChanged();
            }
        });
        if(!db.checkRoom(username, room_list.get(position).getId())){
            holder.leave_room.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.contentEquals(room_list.get(position).getUsername())){
                    Intent intent = new Intent(context, MessagesActivity.class);
                    intent.putExtra("room_id", room_list.get(position).getId());
                    intent.putExtra("username", username);
                    context.startActivity(intent);
                }
                else{
                    if(db.checkRoom(username, room_list.get(position).getId())){
                        Intent intent = new Intent(context, MessagesActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("room_id", room_list.get(position).getId());
                        context.startActivity(intent);
                    }else {
                        Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.password_dialog);
                        EditText pass = dialog.findViewById(R.id.enter_room_password);
                        Button enter_room_button = dialog.findViewById(R.id.enter_room_button);
                        enter_room_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pass.getText().toString().contentEquals(room_list.get(position).getPassword())) {
                                    db.addUnlockedRoom(username, room_list.get(position).getId());
                                    Intent intent = new Intent(context, MessagesActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("room_id", room_list.get(position).getId());
                                    dialog.dismiss();
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return room_list.size();
    }
}
