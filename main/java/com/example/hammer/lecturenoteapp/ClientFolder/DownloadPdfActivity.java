package com.example.hammer.lecturenoteapp.ClientFolder;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammer.lecturenoteapp.Model.Pdf;
import com.example.hammer.lecturenoteapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DownloadPdfActivity extends AppCompatActivity {

    TextView course_name, lectName, pdf_description;

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnDownload;


    String pdfId ="";
    FirebaseDatabase database;
    DatabaseReference PdfDoc;
    Pdf currentDoc;
    private Toolbar mtoolbar;
    //list to store uploads data



    private static final int ACTIVITY_NUM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_pdf);

        //        TOOLBAR
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setup the backarrow for navigating back to prof....
        ImageView backArrow = (ImageView) findViewById(R.id.back_btn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdfId =  getIntent().getStringExtra("PdfId");

        //Firebase

        database = FirebaseDatabase.getInstance();
        PdfDoc =  database.getReference("Year");


        //init view
        btnDownload =  (FloatingActionButton)findViewById(R.id.btnCart);



        pdf_description = (TextView) findViewById(R.id.pdf_description);
        lectName = (TextView) findViewById(R.id.LectureName);
        course_name = (TextView) findViewById(R.id.courseName);


        collapsingToolbarLayout =  (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);




        //Get Food from Intent

        getDetailDoc(pdfId);

    }

    private void getLink() {

    }

    private void getDetailDoc(final String pdfId) {

        PdfDoc.child(pdfId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentDoc = dataSnapshot.getValue(Pdf.class);

                collapsingToolbarLayout.setTitle(currentDoc.getCourse());

                course_name.setText(currentDoc.getCourse());
                lectName.setText("Lecturer "+ currentDoc.getLecturesName());
                pdf_description.setText(currentDoc.getDescription());
//                btnDownload.setTex

                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DownloadPdfActivity.this, ""+currentDoc.getPdf(), Toast.LENGTH_LONG).show();
//                        Intent download = new Intent
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                          intent.setData(Uri.parse(currentDoc.getPdf()));
                        startActivity(intent);


                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
