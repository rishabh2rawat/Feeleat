package com.rishabhrawat.feeleat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeclarationActivity extends AppCompatActivity {

    TextView name, pin;
    String ssname,spin;
    Button cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration);

        name=(TextView)findViewById(R.id.name);
        pin=(TextView)findViewById(R.id.pin);
        cont=(Button) findViewById(R.id.continue_btn);

        ssname=getIntent().getStringExtra("name");
        spin=getIntent().getStringExtra("pin");

        name.setText(ssname);
        pin.setText(spin);

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(DeclarationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
