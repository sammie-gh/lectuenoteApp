package com.example.hammer.lecturenoteapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammer.lecturenoteapp.Common.Common;
import com.example.hammer.lecturenoteapp.Interface.ItemClickListener;
import com.example.hammer.lecturenoteapp.Model.Pdf;
import com.example.hammer.lecturenoteapp.ViewHolder.ItemsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddpdfList extends AppCompatActivity {


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
////
    Uri saveUri;
    Toolbar mToolbar;
    FirebaseRecyclerAdapter<Pdf, ItemsViewHolder> adapter;

    //Add NE food
    EditText edtName, edtDescription, edtYearGruop, edtLectName;
    Button btnSelect, btnUpload;
    SwipeRefreshLayout MrefreshLayout;
    Pdf newPdf;
    TextView txtInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf_list);


        db = FirebaseDatabase.getInstance();
        PdfList = db.getReference("Year");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        mToolbar = findViewById(R.id.main_app_bar);
        mToolbar.setTitle("Admin Panel");
        setSupportActionBar(mToolbar);

        txtInfo  =  findViewById(R.id.txtInfo);

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerV);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        rootLayout = (RelativeLayout) findViewById(R.id.root_Layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPdfDialog();
            }
        });


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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPdfDialog();
            }
        });


        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty())

            loadListPdf(categoryId);

    }



//    private void getPDF() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                    Uri.parse("package:" + getPackageName()));
//            startActivity(intent);
//            return;
//        }
//        //creating an intent for file chooser
//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
//    }

    private void showAddPdfDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddpdfList.this);
        alertDialog.setTitle("Add  Document");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_pdf_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtLectName = add_menu_layout.findViewById(R.id.edtLectName);
        edtYearGruop = add_menu_layout.findViewById(R.id.edtYear);

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUp);

        //  Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePdf(); //


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
        alertDialog.setPositiveButton("Add new Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//can be yes
                dialogInterface.dismiss();

                //Here ,just create ne cat...
                if (newPdf != null) {
                    PdfList.push().setValue(newPdf);
                    Snackbar.make(MrefreshLayout, "New Documents" + newPdf.getCourse() + "was added successful", Snackbar.LENGTH_LONG)
                            .show();
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

    private void uploadPdf() {
        if (saveUri != null)

        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String pdfName = UUID.randomUUID().toString();
            final StorageReference pdfFolder = storageReference.child("pdf/" + pdfName);
            pdfFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(AddpdfList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            pdfFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set vallue for new Category if pdf is pdf and we can get download link
                                    newPdf = new Pdf();
                                    newPdf.setCourse(edtName.getText().toString());
                                    newPdf.setDescription(edtDescription.getText().toString());
                                    newPdf.setLecturesName(edtLectName.getText().toString());
                                    newPdf.setDate(categoryId);//Idset date as munui d
                                    newPdf.setYearGroup(edtYearGruop.getText().toString());
                                    newPdf.setPdf(uri.toString());//see which works
//                                    mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
//                                    PdfList.child(newPdf.getUrl()).setValue(newPdf);




                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(AddpdfList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("pdf Selected !");

        }


    }
    private void choosePdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf Document"), Common.PICK_IMAGE_REQUEST);

    }

//    private void uploadFile() {
//        if (saveUri != null) {
//            final ProgressDialog mDialog = new ProgressDialog(this);
//            mDialog.setMessage("Uploading...");
//            mDialog.show();
//
//            StorageReference sRef = storageReference.child(Common.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
//            sRef.putFile(saveUri)
//
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            mDialog.dismiss();
//                            Toast.makeText(AddpdfList.this, "Uploaded", Toast.LENGTH_SHORT).show();
//
//
//                            newPdf = new Pdf();
//                            newPdf.setCourse(edtName.getText().toString());
//                            newPdf.setDescription(edtDescription.getText().toString());
//                            newPdf.setLecturesName(edtYearGruop.getText().toString());
//                            newPdf.setDate(edtLectName.getText().toString());
//                            newPdf.setYearGroup(categoryId);
//                            PdfList.child(PdfList.push().getKey()).setValue(newPdf);
//
//                            PdfList.child(PdfList.push().getKey()).setValue(newPdf);
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            mDialog.setMessage("Uploaded" + progress + "%");
//                        }
//                    });
//        }
//    }

    private void loadListPdf(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Pdf, ItemsViewHolder>(
                Pdf.class,
                R.layout.pdf_item,
                ItemsViewHolder.class,
                PdfList.orderByChild("date").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(ItemsViewHolder viewHolder, Pdf model, int position) {

                viewHolder.pdfName.setText("Title : " + model.getCourse());
                viewHolder.LecName.setText("Lect.name : " + model.getLecturesName());
                viewHolder.Year.setText("Level " + model.getYearGroup());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {

            deletePdf(adapter.getRef(item.getOrder()).getKey());
            //add toast later
        }
        return super.onContextItemSelected(item);
    }

    private void deletePdf(String key) {
        PdfList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Pdf item) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddpdfList.this);
        alertDialog.setTitle("Edit Doc");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_pdf_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtLectName = add_menu_layout.findViewById(R.id.edtLectName);
        edtYearGruop = add_menu_layout.findViewById(R.id.edtYear);

        //set default value for view
        edtName.setText(item.getCourse());
        edtLectName.setText(item.getLecturesName());
        edtYearGruop.setText(item.getYearGroup());
        edtDescription.setText(item.getDescription());


        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUp);

        //  Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePdf(); // let user select image from gallery and save URI of this


            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePdf(item);
            }

        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_picture_as_pdf_black_24dp);

        //Set button
        alertDialog.setPositiveButton("Add new Pdf", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//can be yes
                dialogInterface.dismiss();

                //update info...
                item.setCourse(edtName.getText().toString());
                item.setYearGroup(edtYearGruop.getText().toString());
                item.setLecturesName(edtLectName.getText().toString());
                item.setDescription(edtDescription.getText().toString());


                PdfList.child(key).setValue(item);

                Snackbar.make(MrefreshLayout, " Document" + item.getCourse() + "was edited successful", Snackbar.LENGTH_LONG)
                        .show();


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

    private void UpdatePdf(final Pdf item) {
        if (saveUri != null)

        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imgName = UUID.randomUUID().toString();
            final StorageReference pdfFolder = storageReference.child("pdf/" + imgName);
            pdfFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(AddpdfList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            pdfFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new Category if image is image and we can get download link
                                    item.setPdf(uri.toString());


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(AddpdfList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });
        }
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