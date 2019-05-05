package ensharp.yeey.whisperer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
