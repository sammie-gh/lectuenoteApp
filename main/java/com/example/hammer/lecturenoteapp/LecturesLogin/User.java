package com.example.hammer.lecturenoteapp.LecturesLogin;

/**
 * Created by A.Richard on 19/09/2017.
 */

public class User {
    private String Password,Phone,IsStaff;

    public User(String name, String password) {

        Password = password;
    }

    public User() {
    }



    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}
