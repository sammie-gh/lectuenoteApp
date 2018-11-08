package com.example.hammer.lecturenoteapp;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hammer.lecturenoteapp.Common.Common;

public class StudentRecordsActivity extends AppCompatActivity {

    EditText Cname, indexNumber,emarks;
    Button add,view,viewall,Show1,delete,modify;
    TextView adminLogin;
    SQLiteDatabase db;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_records);

        Cname =(EditText)findViewById(R.id.name);
        indexNumber =(EditText)findViewById(R.id.roll_no);
        emarks=(EditText)findViewById(R.id.marks);
        add=(Button)findViewById(R.id.addbtn);
        view=(Button)findViewById(R.id.viewbtn);
        viewall=(Button)findViewById(R.id.viewallbtn);
        delete=(Button)findViewById(R.id.deletebtn);
        Show1=(Button)findViewById(R.id.showbtn);
        modify=(Button)findViewById(R.id.modifybtn);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Student Records");

//to underline
        adminLogin = findViewById(R.id.Nb);
        SpannableString content = new SpannableString("Please Note details entered does not save to online Database for privacy reasons Thank you");
        content.setSpan(new UnderlineSpan(),0 , content.length(),0);
        adminLogin.setText(content);

        db=openOrCreateDatabase("Student_manage", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno INTEGER,name VARCHAR,marks INTEGER);");


        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(indexNumber.getText().toString().trim().length()==0||
                        Cname.getText().toString().trim().length()==0||
                        emarks.getText().toString().trim().length()==0)
                {
                    showMessage("Error", "Please enter all values");
                    return;
                }
                db.execSQL("INSERT INTO student VALUES('"+ indexNumber.getText()+"','"+ Cname.getText()+
                        "','"+emarks.getText()+"');");
                showMessage("Success", "Record added successfully");
                clearText();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(indexNumber.getText().toString().trim().length()==0)
                {
                    showMessage("Error", "Please enter StudentId");//Rollno
                    return;
                }
                Cursor c=db.rawQuery("SELECT * FROM student WHERE StudentId='"+ indexNumber.getText()+"'", null);
                if(c.moveToFirst())
                {
                    db.execSQL("DELETE FROM student WHERE StudentId='"+ indexNumber.getText()+"'");
                    showMessage("Success", "Record Deleted");
                }
                else
                {
                    showMessage("Error", "Invalid StudentId");
                }
                clearText();
            }
        });
        modify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(indexNumber.getText().toString().trim().length()==0)
                {
                    showMessage("Error", "Please enter StudentId");
                    return;
                }
                Cursor c=db.rawQuery("SELECT * FROM student WHERE StudentId='"+ indexNumber.getText()+"'", null);
                if(c.moveToFirst())
                {
                    db.execSQL("UPDATE student SET name='"+ Cname.getText()+"',marks='"+emarks.getText()+
                            "' WHERE StudentId='"+ indexNumber.getText()+"'");
                    showMessage("Success", "Record Modified");
                }
                else
                {
                    showMessage("Error", "Invalid Student number");
                }
                clearText();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(indexNumber.getText().toString().trim().length()==0)
                {
                    showMessage("Error", "Please enter StudentId");//Rollno
                    return;
                }
                Cursor c=db.rawQuery("SELECT * FROM student WHERE StudentId='"+ indexNumber.getText()+"'", null);
                if(c.moveToFirst())
                {
                    Cname.setText(c.getString(1));
                    emarks.setText(c.getString(2));
                }
                else
                {
                    showMessage("Error", "Invalid StudentId");
                    clearText();
                }
            }
        });
        viewall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Cursor c=db.rawQuery("SELECT * FROM student", null);
                if(c.getCount()==0)
                {
                    showMessage("Error", "No records found");
                    return;
                }
                StringBuffer buffer=new StringBuffer();
                while(c.moveToNext())
                {
                    buffer.append("StudentId: "+c.getString(0)+"\n");
                    buffer.append("Name: "+c.getString(1)+"\n");
                    buffer.append("Marks: "+c.getString(2)+"\n\n");
                }
                showMessage("Student Details", buffer.toString());
            }
        });
        Show1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showMessage("Student Management Application", "Developed By Mr. Evans");
            }
        });

    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clearText()
    {
        indexNumber.setText("");
        Cname.setText("");
        emarks.setText("");
        indexNumber.requestFocus();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_main, menu);
        return true;
    }

}