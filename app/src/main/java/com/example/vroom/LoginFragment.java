package com.example.vroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private Button login_button;
    private DatabaseHelper databaseHelper;


    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        username = (EditText) view.findViewById(R.id.login_username);
        password = (EditText) view.findViewById(R.id.login_password);
        login_button = (Button) view.findViewById(R.id.login_button);
        initListeners();
        initObjects();
        return view;
    }


    private void initListeners(){
        login_button.setOnClickListener(this);
    }

    private void initObjects(){
        databaseHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                InputValidation iv = new InputValidation();
                if(iv.isInputFilled(username.getText().toString())){
                    if(iv.isInputFilled(password.getText().toString())){
                        verifyfromSQLite();
                    }
                    else{
                        Toast.makeText(getContext(),"Password is empty!",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getContext(),"Username is empty!",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void verifyfromSQLite(){
        if (databaseHelper.checkUser(username.getText().toString().trim(), password.getText().toString().trim())){
            Intent intent = new Intent(getActivity(), RoomsActivity.class);
            intent.putExtra("username", username.getText().toString());
            startActivity(intent);
        }
        else{
            Toast.makeText(getContext(),"Invalid login credentials",Toast.LENGTH_LONG).show();
        }
    }
}
