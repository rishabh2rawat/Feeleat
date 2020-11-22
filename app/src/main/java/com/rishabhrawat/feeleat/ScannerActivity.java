package com.rishabhrawat.feeleat;

import java.io.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicMarkableReference;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;


public class ScannerActivity extends AppCompatActivity {


String Faceid1;
String faceid2;

    private CameraKitView cameraKitView;
    Button verify;

    String name;
    TextView sname;

    StorageReference islandRef;
    int imageid=0;

    String faceid1;
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    private final String apiEndpoint = "https://centralindia.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "eb24b0ef3dad41e088f87b088c124825";

    private final FaceServiceClient faceServiceClient =
            new FaceServiceRestClient(apiEndpoint, subscriptionKey);






    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detectionProgressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_scanner);





/*----connecting ui------------------------------
         * connecting ui*/
        cameraKitView = findViewById(R.id.camera);
        verify = (Button) findViewById(R.id.verify);
        sname = (TextView) findViewById(R.id.name);




/*intenent data*/
        name = getIntent().getStringExtra("name");

        sname.append(name);


/*
----------------on click verify----------*/
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] photo) {

                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                        try {
                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(photo);
                            outputStream.close();


                            Bitmap picBitmap = BitmapFactory.decodeFile(savedPhoto.getPath());




                            detectAndFrame(picBitmap,0);




                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("CKDemo", "Exception in photo callback");
                        }


                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);



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



                        detectionProgressDialog.dismiss();
                        if(!exceptionMessage.equals("")){
                            showError(exceptionMessage);
                        }
                        if (result == null) return;


                        for (Face face : result) {
                            String faceid2=face.faceId.toString();

                            verify(faceid2);
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

    void verify(String faceid2)
    {
        String faceid1=getIntent().getStringExtra("faceid1");


        UUID f1= UUID.fromString(faceid1);
        UUID f2=UUID.fromString(faceid2);

        VerificationTask task=new VerificationTask(f1,f2);
        task.execute();


        Toast.makeText(this, "verifying", Toast.LENGTH_SHORT).show();

    }


    private class VerificationTask extends AsyncTask<Void, String, VerifyResult>

    {
        // The IDs of two face to verify.
        private UUID mFaceId0;
        private UUID mFaceId1;

        VerificationTask (UUID faceId0, UUID faceId1) {
            mFaceId0 = faceId0;
            mFaceId1 = faceId1;
        }

        @Override
        protected VerifyResult doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            try{
                publishProgress("Verifying...");

                // Start verification.
                return faceServiceClient.verify(
                        mFaceId0,      /* The first face ID to verify */
                        mFaceId1);     /* The second face ID to verify */
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(String... progress) {


        }

        @Override
        protected void onPostExecute(VerifyResult result) {
            if (result != null) {
                String str=result.isIdentical ? "same person" : "not same person";
                Toast.makeText(ScannerActivity.this, str, Toast.LENGTH_SHORT).show();

                if(str.equals("same person"))
                {
                    Intent intent=new Intent(ScannerActivity.this,ScheduleActivity.class);

                    intent.putExtra("name",getIntent().getStringExtra("name"));
                    intent.putExtra("pin",getIntent().getStringExtra("pin"));
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(ScannerActivity.this, "Verification failed please try again", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }




}
