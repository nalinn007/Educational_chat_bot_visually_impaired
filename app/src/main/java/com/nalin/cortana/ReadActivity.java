package com.tushar.cortana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ReadActivity extends AppCompatActivity {

    int c=1;
    String tot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ScanActivity sc=new ScanActivity();
        final TextView tx=(TextView)findViewById(R.id.textView7);
        final TextView sentence=(TextView)findViewById(R.id.textView8);

        final Button br1=(Button)findViewById(R.id.button6);
        Button br2=(Button)findViewById(R.id.button7);

        ImageButton bac=(ImageButton)findViewById(R.id.imageButton311);
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ReadActivity.this, ScanActivity.class);
                startActivity(i);

            }
        });

        String str=sc.msg;
        str=str.toUpperCase();
        Log.d("QWER",str);

        final String[] arr=str.split(" ");
        String e=arr[0];
        tot=arr[0];

        char[] arr1=e.toCharArray();
        StringBuilder str1=new StringBuilder();
        for(int i=0;i<arr1.length;i++){
            str1.append(arr1[i]);
            str1.append(" ");
        }
        tx.setText(str1);
        sentence.setText(tot);

        br1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e=arr[c];

                char[] arr1=e.toCharArray();
                StringBuilder str1=new StringBuilder();
                for(int i=0;i<arr1.length;i++){
                    str1.append(arr1[i]);
                    str1.append(" ");
                }
                tx.setText(str1);
                tot=tot+" "+e;
                sentence.setText(tot);
                c=c+1;
                if(c>=arr.length){
                    br1.setVisibility(View.INVISIBLE);
                }
            }
        });

        br2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ReadActivity.this,SelectActivity.class);
                startActivity(i);

            }
        });

    }
}
