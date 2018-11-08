package com.example.hammer.lecturenoteapp.Model;

/**
 * Created by A.Richard on 03/09/2017.
 */

public class Department {

    private String Name;
    private String Image;


    public Department() {
    }

    public Department(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
