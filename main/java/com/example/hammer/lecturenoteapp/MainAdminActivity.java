package com.example.hammer.lecturenoteapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammer.lecturenoteapp.Common.Common;
import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.Model.Department;
import com.example.hammer.lecturenoteapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class MainAdminActivity extends AppCompatActivity {


    TextView txtFullName;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Department,MenuViewHolder> adapter;

    //View
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;

    //add new menu layout
    EditText edtName;
    TextView txtInfo;
    Button btnUpload,btnSelect;

    Department newCategory;

    Uri saveUri;
    Toolbar mToolbar;
    SwipeRefreshLayout swipeLayout;
    FloatingActionMenu mMenu;
    com.github.clans.fab.FloatingActionButton mAdd,mRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        //init firebase
        database =   FirebaseDatabase.getInstance();
        categories = database.getReference("Department");

        storage =  FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //init View
        recyclerView_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyclerView_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_menu.setLayoutManager(layoutManager);
        txtInfo  = findViewById(R.id.txtInfo);

        loadDepartmentlist();

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                       getSupportActionBar().setTitle(Common.currentUser.getPhone());

                       mMenu = findViewById(R.id.menu);
                       mAdd=  findViewById(R.id.menu_item_add);
                       mRecords = findViewById(R.id.menu_item_record);
    mRecords.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainAdminActivity.this,StudentRecordsActivity.class);
            startActivity(intent);
        }
    });

    mAdd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog();
        }
    });

        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.white,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDepartmentlist();
            }
        });
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {

                loadDepartmentlist();

            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });

    }

    private void showDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainAdminActivity.this);
        alertDialog.setTitle("Add new Department");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_department_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect =  add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUp);

        //  Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); // let user select image from gallery and save URI of this


            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPdf();
            }

        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_picture_as_pdf_black_24dp);

        //Set button
        alertDialog.setPositiveButton("Add new Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//can be yes
                dialogInterface.dismiss();

                //Here ,just create ne cat...
                if (newCategory != null){
                    categories.push().setValue(newCategory);
                    Toast.makeText(MainAdminActivity.this, "New Department"+ newCategory.getName()+ "was added ", Toast.LENGTH_SHORT).show();
                }


            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();

    }

    //update /delete

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            UpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }else  if (item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        //First , we need to get all food in category
        DatabaseReference cloths =  database.getReference("Foods");
        Query foodIncategory = cloths.orderByChild("menuId").equalTo(key);
        foodIncategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren())
                {
                    postSnap.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        categories.child(key).removeValue();
        Toast.makeText(this, "Item deleted !!!", Toast.LENGTH_SHORT).show();
    }

    private void UpdateDialog(final String key, final Department item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainAdminActivity.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_department_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect =  add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUp);

        //set default name
        edtName.setText(item.getName());

        //  Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(); // let user select image from gallery and save URI of this


            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(item);
            }

        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_picture_as_pdf_black_24dp);

        //Set button
        alertDialog.setPositiveButton("Add new Department", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //can be yes
                dialogInterface.dismiss();


                //Update information
                item.setName(edtName.getText().toString());
                categories.child(key).setValue(item);

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();

    }

    private void changeImage(final Department item) {

        if (saveUri != null)

        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imgName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(MainAdminActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new Category if image is image and we can get download link
                                    item.setImage(uri.toString());


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(MainAdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress+ "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() !=null){
            saveUri = data.getData();
            btnSelect.setText("Image Selected !");

        }

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");//pdf
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Department Picture"),Common.PICK_IMAGE_REQUEST);


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
                Picasso.with(MainAdminActivity.this).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send cate... to new intent
                        Intent intent = new Intent(MainAdminActivity.this,AddpdfList.class);
                        intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

            }
        };

        adapter.notifyDataSetChanged(); //Refresh data if have data changed
        recyclerView_menu.setAdapter(adapter);

    }

    private void uploadPdf() {

        if (saveUri != null)

        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imgName);//images//
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(MainAdminActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set vallue for new Category if image is image and we can get download link
                                    newCategory = new Department(edtName.getText().toString(),uri.toString());


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(MainAdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress+ "%");
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adminlog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logOut) {
            //logout
            Intent signIn = new Intent(MainAdminActivity.this, LoginActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {

        Hide();
        super.onStart();
    }

    private void Hide() {

        //time to close view
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
//                timer.setText("0:" + l / 1000 + " s");
//                resendCode.setVisibility(View.INVISIBLE);
            }


            //call to hide views
            @Override
            public void onFinish() {
                txtInfo.setVisibility(View.GONE);


            }
        }.start();
        //timer ends here

    }
}
