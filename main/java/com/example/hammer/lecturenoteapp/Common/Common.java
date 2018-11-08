package com.example.hammer.lecturenoteapp.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.hammer.lecturenoteapp.LecturesLogin.User;


/**
 * Created by A.Richard on 19/09/2017.
 */

public class Common {

    public static User currentUser;


    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST = 10000;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static final String STORAGE_PATH_UPLOADS = "docsPdf/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";



}