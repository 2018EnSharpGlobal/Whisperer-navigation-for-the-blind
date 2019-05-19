package ensharp.yeey.whisperer.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ensharp.yeey.whisperer.CommandWaitingActivity;
import ensharp.yeey.whisperer.R;
import ensharp.yeey.whisperer.WatsonAssistant;

import static java.lang.Thread.sleep;

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        Intent intent = new Intent(IntroActivity.this, WatsonAssistant.class);
        startActivity(intent);

        // Intro screen
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, CommandWaitingActivity.class));
        finish();
    }
}
