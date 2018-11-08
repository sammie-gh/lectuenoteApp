package com.example.hammer.lecturenoteapp.ClientFolder;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.Model.Pdf;
import com.example.hammer.lecturenoteapp.R;
import com.example.hammer.lecturenoteapp.ViewHolder.ItemsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PdfViewList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

//    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference PdfList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";

    RelativeLayout rootLayout;
    Uri saveUri;
    Toolbar mToolbar;
    FirebaseRecyclerAdapter<Pdf, ItemsViewHolder> adapter;

    SwipeRefreshLayout MrefreshLayout;
    Pdf newPdf;


    //Search Functionality
    FirebaseRecyclerAdapter<Pdf,ItemsViewHolder> searchAdapter;
    List<String> suggestion = new ArrayList<>();
//    MaterialSearchBar materialSearchBar;

    private static final int ACTIVITY_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view_list);

        db = FirebaseDatabase.getInstance();
        PdfList = db.getReference("Year");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mToolbar = findViewById(R.id.main_app_bar);
        mToolbar.setTitle("Materials");
        setSupportActionBar(mToolbar);

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        MrefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        MrefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.white,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        MrefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        MrefreshLayout.post(new Runnable() {
            @Override
            public void run() {

            }
        });

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty()) {

            loadListPdf(categoryId);

        }
        //remove if crash
    }

    private void loadListPdf(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Pdf, ItemsViewHolder>(
                Pdf.class,
                R.layout.pdf_item,
                ItemsViewHolder.class,
                PdfList.orderByChild("date").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(ItemsViewHolder viewHolder, Pdf model, int position) {

                viewHolder.pdfName.setText(model.getCourse());
                viewHolder.LecName.setText("Lect.name : " + model.getLecturesName());
                viewHolder.Year.setText("Level" + model.getYearGroup());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new Activity
                        Intent download  = new Intent(PdfViewList.this,DownloadPdfActivity.class);
                        download.putExtra("PdfId",adapter.getRef(position).getKey());// send food Id to new new act...
                        startActivity(download);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}
