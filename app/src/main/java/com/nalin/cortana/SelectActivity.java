package com.tushar.cortana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Button br1=(Button)findViewById(R.id.button2);
        Button br2=(Button)findViewById(R.id.button3);
        ImageButton img=(ImageButton)findViewById(R.id.imageButton21);

        br1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectActivity.this,MainPage.class);
                startActivity(i);
            }
        });

        br2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectActivity.this,ScanActivity.class);
                startActivity(i);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SelectActivity.this,OptionsActivity.class);
                startActivity(i);
            }
        });

    }
}
