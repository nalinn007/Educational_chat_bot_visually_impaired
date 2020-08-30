package com.tushar.cortana;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    Button button;
    TextView op;
    EditText ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        op=(TextView)findViewById(R.id.textView);
        ip=(EditText)findViewById(R.id.editText2);
        button=(Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("The text to speech "+ip.getText().toString());
                op.setText("TTS "+ip.getText().toString());

            }
        });
    }
}
