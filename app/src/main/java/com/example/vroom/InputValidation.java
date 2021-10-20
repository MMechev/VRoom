package com.example.vroom;


public class InputValidation {

    public InputValidation(){

    }

    public boolean isInputFilled(String input){
        if(input.isEmpty()){
            return false;
        }
        else return true;
    }

    public boolean confirmPassword(String password, String confirm_password){
        if(password.contentEquals(confirm_password)){
            return true;
        }
        else return false;
    }
}
