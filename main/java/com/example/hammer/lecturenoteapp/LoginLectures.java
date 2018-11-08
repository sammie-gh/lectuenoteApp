package com.example.hammer.lecturenoteapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hammer.lecturenoteapp.Common.Common;
import com.example.hammer.lecturenoteapp.LecturesLogin.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginLectures extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private ProgressBar loginProgress;
    EditText edtPhone,edtPassword;
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_login_lectures);

        edtPassword = (EditText) findViewById(R.id.reg_pass);
        edtPhone = (EditText) findViewById(R.id.reg_phone);
        btnSignIn = (Button) findViewById(R.id.login_btn);
        loginProgress = findViewById(R.id.login_progress);

        Paper.init(this);
        //Init Firebase

        db = FirebaseDatabase.getInstance();
        users = db.getReference("Administator");//Users

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUser(edtPhone.getText().toString(), edtPassword.getText().toString());

            }
        });
    }

    private void signUser(final String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        loginProgress.setVisibility(View.VISIBLE);

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();
                    User user =  dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if (Boolean.parseBoolean(user.getIsStaff())) //if isStaff == true
                    {
                        if (user.getPassword().equals(localPassword)){
                            //Delete remember user
                            Paper.book().destroy();

                            Intent login = new Intent(LoginLectures.this,MainAdminActivity.class);
                            Common.currentUser = user;
                            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            finish();
                        }
                        //revert here
                    }
                    else loginProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginLectures.this, " ", Toast.LENGTH_SHORT).show();


                }
                else {
                    mDialog.dismiss();
                    loginProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginLectures.this, "User not exist in Database \n contact Admin or try again", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginLectures.this, "Please login with Staff account \n or contact Admin", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
