package com.example.vroom;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;

public class ViewHolderIncomingMessage extends RecyclerView.ViewHolder {

    public TextView message;
    public TextView date;
    public TextView msg_username;

    public ViewHolderIncomingMessage(@NonNull View itemView) {
        super(itemView);
        message = (TextView) itemView.findViewById(R.id.message_text);
        date = (TextView) itemView.findViewById(R.id.date_text);
        msg_username = (TextView) itemView.findViewById(R.id.username);
    }

    void bind(Message msg, String username) {
        message.setText(msg.getMessage());
        date.setText(msg.getDate().replace("GMT+02:00", ""));
        msg_username.setText("u/" + msg.getUsername());
    }
}
