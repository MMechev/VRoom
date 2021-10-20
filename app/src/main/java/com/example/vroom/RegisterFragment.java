package com.example.vroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText username;
    private EditText password;
    private EditText confirm_password;
    private Button register_button;
    private DatabaseHelper databaseHelper;
    private User user;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);
        username = (EditText) view.findViewById(R.id.register_username);
        password = (EditText) view.findViewById(R.id.register_password);
        confirm_password = (EditText) view.findViewById(R.id.register_confirm_password);
        register_button = (Button) view.findViewById(R.id.register_button);
        initListeners();
        initObjects();
        return view;
    }

    private void initListeners(){
        register_button.setOnClickListener(this);
    }

    private void initObjects(){
        databaseHelper = new DatabaseHelper(getActivity());
        user = new User();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                InputValidation iv = new InputValidation();
                if(iv.isInputFilled(username.getText().toString())){
                    if(iv.isInputFilled(password.getText().toString())){
                        if(iv.isInputFilled(confirm_password.getText().toString())){
                            if(iv.confirmPassword(password.getText().toString(),confirm_password.getText().toString())){
                                verifyfromSQLite();
                            }
                            else{
                                Toast.makeText(getContext(),"Passwords do not match!",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(getContext(),"Confirm password is empty!",Toast.LENGTH_LONG).show();
                        }
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
        if (databaseHelper.checkUser(username.getText().toString().trim())){
            Toast.makeText(getActivity(),"The username is already in use!",Toast.LENGTH_LONG).show();
        }
        else{
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());
            databaseHelper.addUser(user);
            Toast.makeText(getActivity(),"Account successfully registered!",Toast.LENGTH_LONG).show();
        }
    }
}