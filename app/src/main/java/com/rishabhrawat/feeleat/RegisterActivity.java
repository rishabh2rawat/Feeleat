package com.rishabhrawat.feeleat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";
    private int REQUEST_CAMERA = 10, SELECT_FILE = 1;
    private String userChoosenTask;
    private CircleImageView profileimage;
    Bitmap thumbnail;
    TextView uploadimage;
    Uri FilePathUri;
    String photourl;
    // Folder path for Firebase Storage.
    String Storage_Path = "";
    ProgressDialog progressDialog;
    Button register;
    EditText name,phoneno,email,address;
    String sname,sphoneno,semail,saddress;
    ProgressBar progressBar;
    FirebaseFirestore db;
    addUser addUserobg;
    nextmpin nextmpinobj;
    nextmpin updateobj;
    int check=0;

    StringBuilder next = new StringBuilder("");

    String nextpin="10000";

    // Creating StorageReference and DatabaseReference object.
     private StorageReference storageReference;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*linking to layout*/
        profileimage = (CircleImageView) findViewById(R.id.profile_image);

        register = (Button) findViewById(R.id.register);
        uploadimage = (TextView) findViewById(R.id.uploadimage);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        name=(EditText)findViewById(R.id.name);
        phoneno=(EditText)findViewById(R.id.phoneno);
        email=(EditText)findViewById(R.id.email);
        address=(EditText)findViewById(R.id.address);
        addUserobg=new addUser();
        progressBar=(ProgressBar)findViewById(R.id.progressbar);


        storageReference = FirebaseStorage.getInstance().getReference();

        db = FirebaseFirestore.getInstance();

        getPin();


/**********************on click image***********************************/
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage.setText("Click Here to Confirm Image");
                uploadimage.setTextColor(Color.BLUE);
                selectImage();
            }
        });



        /**************************************Uploading profile image to the firebase and getting a uniqe url*********************/

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thumbnail != null) {
                    //storageReference = FirebaseStorage.getInstance().getReference();
                    UploadImageFileToFirebaseStorage();
                } else {
                    Toast.makeText(RegisterActivity.this, "first take a profile photo", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /*----------------------------------------on Click register------------------------------------------------------------*/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sname=name.getText().toString().trim();
                sphoneno=phoneno.getText().toString().trim();
                semail=email.getText().toString().trim();
                saddress=address.getText().toString().trim();

                if(sname.isEmpty()||sphoneno.isEmpty()||semail.isEmpty()||saddress.isEmpty()||check==0)
                {
                    Toast.makeText(RegisterActivity.this, "Enter all the details", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    progressBar.setVisibility(View.VISIBLE);
                    addUserobg.setName(sname);
                    addUserobg.setPhoneno(sphoneno);
                    addUserobg.setEmail(semail);
                    addUserobg.setAddress(saddress);
                    addUserobg.setSphotourl(photourl);


                    addUser();

                }
            }
        });

    }


    /*********************select image method*************************************************************/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                            cameraIntent();
                        } else {
                            String[] permitionRequested = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permitionRequested, REQUEST_CAMERA);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            galleryIntent();
                        } else {
                            String[] permitionRequested = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permitionRequested, SELECT_FILE);
                        }
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: //Request camera code
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo")) {
                        cameraIntent();
                    }

                }
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Choose from Library")) {
                        galleryIntent();
                    }
                    break;
                }
        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");


        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePathUri = getImageUri(getApplicationContext(), thumbnail);
        profileimage.setImageBitmap(thumbnail);
    }

    /*////////////////////////////////geting uri from bitmap///////////////////////////////////////*/
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FilePathUri = data.getData();
        profileimage.setImageBitmap(thumbnail);
    }

    /***************************************upload image to firebase storage*******************************************/

    public void UploadImageFileToFirebaseStorage() {
        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();





            // Creating second StorageReference.
            final StorageReference storageReference2nd = storageReference.child(Storage_Path + nextpin + ".jpg");

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressLint("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            uploadimage.setText("Image Confirmed");
                            uploadimage.setTextColor(Color.GREEN);

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    photourl=downloadUrl.toString();

                                    check=1;
                                    System.out.println(photourl+"====================================================================================================================================================");
                                }
                            });

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully url=" + photourl, Toast.LENGTH_LONG).show();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(RegisterActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        } else {

            Toast.makeText(RegisterActivity.this, "Please Select Image", Toast.LENGTH_LONG).show();

        }
    }


    /*=========================save user to firebase=-=====================*/

    private void getPin() {

        /*generating a new Mpin*/

        nextmpinobj=new nextmpin();
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("mpin").document("pin");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();



                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        nextmpinobj = document.toObject(nextmpin.class);

                        nextpin=nextmpinobj.getMpin();
                        int a=Integer.valueOf(nextpin);
                        a=a+1;

                        String update=String.valueOf(a);

                        updateobj=new nextmpin();
                        updateobj.setMpin(update);

                        /*update nextpin in the firstore*/
                        updatempin();



                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




    }

    private void updatempin() {
        if (updateobj != null) {
            try {
                db = FirebaseFirestore.getInstance();
                final DocumentReference saveVisitReference = db.
                        collection("mpin").
                        document("pin");


                saveVisitReference.set(updateobj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                          System.out.println("pin updated");
                        }
                        else
                        {
                            System.out.println("not updated");
                        }

                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("-------------------------------------------------------");
            }
        }
    }


    public void addUser()
    {
        /*saving new user with new pin*/
        if (addUserobg != null) {
            try {
                db = FirebaseFirestore.getInstance();
                addUserobg.setPin(nextpin);
                final DocumentReference saveUserReference = db.
                        collection("users").
                        document(nextpin);


                saveUserReference.set(addUserobg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {



                            progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(RegisterActivity.this);
                            dlgAlert.setMessage("User Sucsessfully added");
                            dlgAlert.setTitle("Confirmation");
                            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    name.setText("");
                                    phoneno.setText("");
                                    email.setText("");
                                    address.setText("");
                                    progressBar.setVisibility(View.INVISIBLE);

                                    Intent intent =new Intent(RegisterActivity.this,DeclarationActivity.class);
                                    intent.putExtra("name",sname);
                                    intent.putExtra("pin", nextpin);
                                    startActivity(intent);
                                }
                            });

                            dlgAlert.create().show();
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "not added", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("-------------------------------------------------------");
            }
        }
    }


}
