package com.example.neel.myiiit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText username_box,pswd_box;
    Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_box=findViewById(R.id.username_box);
        pswd_box=findViewById(R.id.pswd_box);
        login_btn=findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=username_box.getText().toString();
                String pswd=pswd_box.getText().toString();
                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
                startActivity(intent);
            }
        });
    }
}
