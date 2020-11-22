package com.rishabhrawat.feeleat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    TextView name,checkstatus,time;
    String sname,stime,mpin;
    FirebaseFirestore db;
    schedule scheduleobj,scheduleckout;
    String date;


    Button c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        name=(TextView)findViewById(R.id.name);
        checkstatus=(TextView)findViewById(R.id.updatecheck);
        time=(TextView)findViewById(R.id.time);
        c=(Button)findViewById(R.id.continue_btn);

        sname=getIntent().getStringExtra("name");
        mpin=getIntent().getStringExtra("pin");

        name.setText(sname);

        scheduleckout=new schedule();
        scheduleobj=new schedule();

        db = FirebaseFirestore.getInstance();


        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final String datetime = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date());


        time.setText(datetime);
        DocumentReference docRef = db.collection(mpin).document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        scheduleckout=document.toObject(schedule.class);

                        /*--------------checkout--------------*/


                      checkstatus.setText("CHECKED OUT");

                      scheduleckout.setCheckout(datetime);


                    savecheckout();



                    } else {
                        checkstatus.setText("CHECKED IN");
                        Log.d(TAG, "No such document");
                        scheduleobj.setCheckin(datetime);
                        scheduleobj.setCheckout(datetime);
                        savecheckin();



                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScheduleActivity.this, "conection fail", Toast.LENGTH_SHORT).show();
            }
        });


        /*on click continue*/

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ScheduleActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }

    public void savecheckin()
    {
        if (scheduleobj != null) {
            try {
                db = FirebaseFirestore.getInstance();
                final DocumentReference saveVisitReference = db.
                        collection(mpin).
                        document(date);


                saveVisitReference.set(scheduleobj).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleActivity.this, "chekin connection ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("-------------------------------------------------------");
            }
        }
    }

    public void savecheckout()
    {
        if (scheduleckout != null) {
            try {
                db = FirebaseFirestore.getInstance();
                final DocumentReference saveVisitReference = db.
                        collection(mpin).
                        document(date);


                saveVisitReference.set(scheduleckout).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleActivity.this, "Checkout connection", Toast.LENGTH_SHORT).show();
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
