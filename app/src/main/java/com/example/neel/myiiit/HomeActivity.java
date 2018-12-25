package com.example.neel.myiiit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    String username,pswd;
    Button mess_btn,assgn_btn,attd_btn, mess_cancel_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username=getIntent().getStringExtra("username");
        pswd=getIntent().getStringExtra("pswd");

        mess_btn=findViewById(R.id.mess_btn);
        attd_btn=findViewById(R.id.attd_btn);
        assgn_btn=findViewById(R.id.assgn_btn);
        mess_cancel_btn = findViewById(R.id.mess_cancel_btn);

        mess_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MessActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
                startActivity(intent);
            }
        });

        attd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,AttendenceActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
                startActivity(intent);
            }
        });

        mess_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MessCancelActivity.class );
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
                startActivity(intent);
            }
        });
    }
}
