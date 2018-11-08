package com.example.hammer.lecturenoteapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.Model.Pdf;
import com.example.hammer.lecturenoteapp.ViewHolder.ItemsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class ViewDocumentListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

//Firebase

    FirebaseDatabase db;
    DatabaseReference PdfList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";
    final static int PICK_PDF_CODE = 2342;

    RelativeLayout rootLayout;

    private final int PICK_IMAGE_REQUEST = 71;

    Uri saveUri;
    Toolbar mToolbar;
    FirebaseRecyclerAdapter<Pdf, ItemsViewHolder> adapter;

    //Add NE food
    EditText edtName, edtDescription, edtYearGruop, edtLectName;
    Button btnSelect, btnUpload;
    SwipeRefreshLayout MrefreshLayout;
    Pdf newPdf;


    //Search Functionality
    FirebaseRecyclerAdapter<Pdf,ItemsViewHolder> searchAdapter;
    List<String> suggestion = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document_list);


        db = FirebaseDatabase.getInstance();
        PdfList = db.getReference("Year");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mToolbar = findViewById(R.id.main_app_bar);
        mToolbar.setTitle("Admin Panel");
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


                loadListPdf(categoryId);
            }
        });
        MrefreshLayout.post(new Runnable() {
            @Override
            public void run() {


                loadListPdf(categoryId);
            }
        });

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())

            loadListPdf(categoryId);



        //Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.search_bar);
        materialSearchBar.setHint("Enter your file to search");
        //materialSearcBar.setsp.. to d. cz already de. inn xml
        loadListPdf(categoryId);
        materialSearchBar.setLastSuggestions(suggestion);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //when user type their text, we will change sugesst list
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestion)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);

                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search is closed
                // restore original adapter

                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show of search adapter

//                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });




    }//end of nCreate

//    private void startSearch(CharSequence text) {
//        searchAdapter =  new FirebaseRecyclerAdapter<Pdf, ItemsViewHolder>(
//                Pdf.class,
//                R.layout.pdf_item,
//                ItemsViewHolder.class,
//                PdfList.orderByChild("name").equalTo(text.toString())//compare name
//        ) {
//            @Override
//            protected void populateViewHolder(ItemsViewHolder viewHolder, Pdf model, int position) {
//
//                viewHolder.food_name.setText(model.getName());
//
//
//                final Pdf local = model;
//
//                viewHolder.setItemClickListener(new ItemClickListener() {
//
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        //start new Activity
//                        Intent fooddetail  = new Intent(ViewDocumentListActivity.this,FoodDetail.class);
//                        fooddetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());// send food Id to new new act...
//                        startActivity(fooddetail);
//
//
//                    }
//                });
//            }
//        };
//        recyclerView.setAdapter(searchAdapter); //set adpter for Recyc... is search result
//
//    }


    //Load list pdf
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
                        //code la...
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}
