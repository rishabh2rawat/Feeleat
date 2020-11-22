package com.rishabhrawat.feeleat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class PinActivity extends AppCompatActivity {

    private static final String TAG = "PinActivity";
    EditText enter_mpin;
    ImageView i1, i2, i3, i4;
    String pin;
    Button submit;
    FirebaseFirestore db;
    addUser user;
    ProgressBar progressBar;
    Intent intent;

    String faceid1;
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    private final String apiEndpoint = "https://centralindia.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "eb24b0ef3dad41e088f87b088c124825";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);


        detectionProgressDialog = new ProgressDialog(this);
        i1 = (ImageView) findViewById(R.id.imageview_circle1);
        i2 = (ImageView) findViewById(R.id.imageview_circle2);
        i3 = (ImageView) findViewById(R.id.imageview_circle3);
        i4 = (ImageView) findViewById(R.id.imageview_circle4);
        submit = (Button) findViewById(R.id.submit);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);

        enter_mpin = (EditText) findViewById(R.id.editText_enter_mpin);
        enter_mpin.setFocusable(true);
        enter_mpin.requestFocus();
        enter_mpin.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(enter_mpin, InputMethodManager.SHOW_IMPLICIT);

        enter_mpin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (s.length()) {
                    case 4:
                        i4.setImageResource(R.drawable.circle2);
                        break;
                    case 3:
                        i4.setImageResource(R.drawable.circle);
                        i3.setImageResource(R.drawable.circle2);
                        break;
                    case 2:
                        i3.setImageResource(R.drawable.circle);
                        i2.setImageResource(R.drawable.circle2);
                        break;
                    case 1:
                        i2.setImageResource(R.drawable.circle);
                        i1.setImageResource(R.drawable.circle2);
                        break;
                    default:
                        i1.setImageResource(R.drawable.circle);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                pin = enter_mpin.getText().toString();
                if (pin.length() == 4) {

                    checkPin(pin);

                } else {
                    Toast.makeText(PinActivity.this, "Enter 4 digit pin", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    void checkPin(String mpin) {

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(mpin);
        docRef.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();


                            if (document.exists()) {

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                user=new addUser();
                                user = document.toObject(addUser.class);


                                if (user.getPin().equals(pin)) {
                                    enter_mpin.setText("100");
                                    enter_mpin.setText("10");
                                    enter_mpin.setText("1");
                                    enter_mpin.setText("");

                                    loadimage();



                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    enter_mpin.setText("100");
                                    enter_mpin.setText("10");
                                    enter_mpin.setText("1");
                                    enter_mpin.setText("");
                                    Toast.makeText(PinActivity.this, "Enter a valid mpin", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                enter_mpin.setText("100");
                                enter_mpin.setText("10");
                                enter_mpin.setText("1");
                                enter_mpin.setText("");
                                Toast.makeText(PinActivity.this, "Enter a valid mpin", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }


    void loadimage()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(pin+".jpg");

        final long ONE_MEGABYTE = 720 * 720;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes1) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length);


                detectAndFrame(bitmap,1);

                Toast.makeText(PinActivity.this, "2nd face detected", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PinActivity.this, "connection nortsetup", Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Detect faces by uploading a face image.
// Frame faces after detection.
    private void detectAndFrame(final Bitmap imageBitmap,int id) {


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());


        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>()
        {

            String exceptionMessage = "";

            @Override
            protected Face[] doInBackground(InputStream... params) {
                try {
                    publishProgress("Detecting...");
                    Face[] result = faceServiceClient.detect(
                            params[0],
                            true,         // returnFaceId
                            false,        // returnFaceLandmarks
                            null          // returnFaceAttributes:
                                /* new FaceServiceClient.FaceAttributeType[] {
                                    FaceServiceClient.FaceAttributeType.Age,
                                    FaceServiceClient.FaceAttributeType.Gender }
                                */
                    );

                    if (result == null){
                        publishProgress(
                                "Detection Finished. Nothing detected");
                        return null;
                    }


                    publishProgress(String.format(
                            "Detection Finished. %d face(s) detected",
                            result.length));



                    return result;

                } catch (Exception e) {
                    exceptionMessage = String.format(
                            "Detection failed: %s", e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                //TODO: show progress dialog
                detectionProgressDialog.show();
            }
            @Override
            protected void onProgressUpdate(String... progress) {
                //TODO: update progress
                detectionProgressDialog.setMessage(progress[0]);
            }
            @Override
            protected void onPostExecute(Face[] result) {
                //TODO: update face frames
                Toast.makeText(PinActivity.this, "Face Detected", Toast.LENGTH_SHORT).show();


                detectionProgressDialog.dismiss();
                if(!exceptionMessage.equals("")){
                    showError(exceptionMessage);
                }
                if (result == null) return;

                for (Face face : result) {
                    String faceid1=face.faceId.toString();

                    setValue(faceid1);
                    break;
                }

                imageBitmap.recycle();
            }
        };

        detectTask.execute(inputStream);

    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }})
                .create().show();
    }

    void setValue(String id)
    {
        progressBar.setVisibility(View.INVISIBLE);

        intent = new Intent(PinActivity.this, ScannerActivity.class);

        intent.putExtra("name",user.getName());
        intent.putExtra("url",user.getSphotourl());
        intent.putExtra("pin", pin);
        intent.putExtra("faceid1",id);

        startActivity(intent);
    }



}
