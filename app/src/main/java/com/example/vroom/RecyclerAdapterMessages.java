package com.example.vroom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterMessages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;
    private Activity context;
    private String username;

    public RecyclerAdapterMessages(ArrayList<Message> messages, Activity context, String username) {
        super();
        this.messages = messages;
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1){
            return new ViewHolderOutgoingMessage(LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_message, parent, false));
        }
        else return new ViewHolderIncomingMessage(LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messages.get(position).getUsername().contentEquals(username)) {
            ((ViewHolderOutgoingMessage) holder).bind(messages.get(position), username);
        } else {
            ((ViewHolderIncomingMessage) holder).bind(messages.get(position), username);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(username.contentEquals(messages.get(position).getUsername())){
            return 1;
        }
        else return 2;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
