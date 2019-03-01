package com.example.neel.myiiit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView githubLink = findViewById(R.id.github_link);
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent goToGithub = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.github.com/neel1998/myIIIT"));
                    goToGithub.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    );
                    startActivity(goToGithub);
                } catch (ActivityNotFoundException e) {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.github.com/neel1998/myIIIT")));
                }
            }
        });
    }
}
