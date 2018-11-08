package com.example.hammer.lecturenoteapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hammer.lecturenoteapp.ClientFolder.PdfViewList;
import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.Model.Department;
import com.example.hammer.lecturenoteapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    //Firebase
    FirebaseDatabase database;
    DatabaseReference categories;

    //View
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;
    //Firebase
    private Context mContext;;

    private Toolbar mToolbar;


    FirebaseRecyclerAdapter<Department,MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Department");

        mAuth = FirebaseAuth.getInstance();
        //init Firebase
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Department");



        recyclerView_menu = (RecyclerView)findViewById(R.id.recycler_men);
        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_menu.setLayoutManager(layoutManager);



        loadDepartmentlist();

        //bottomview
//        setupBottomNavigationView();
    }



    private void loadDepartmentlist() {
        adapter = new FirebaseRecyclerAdapter<Department, MenuViewHolder>(
                Department.class,
                R.layout.department_list,//menuviewhlder linked
                MenuViewHolder.class,
                categories
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Department model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(MainActivity.this).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send cate... to new intent
                        Intent intent = new Intent(MainActivity.this, PdfViewList.class);
                        intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

            }
        };

        adapter.notifyDataSetChanged(); //Refresh data if have data changed
        recyclerView_menu.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.refresh)
            loadDepartmentlist();

        if (item.getItemId() == R.id.signOut)
            logOut();
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {


        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }
}