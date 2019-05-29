package ensharp.yeey.whisperer.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ensharp.yeey.whisperer.R;
import ensharp.yeey.whisperer.WatsonAssistant;

import static java.lang.Thread.sleep;

public class IntroActivity extends AppCompatActivity {

    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        background = (ImageView)findViewById(R.id.intro_background);

        Glide.with(this).load(R.drawable.introimage3).into(background);

        // Intro screen
        try {
            sleep(3000);

//            Intent intent = new Intent(IntroActivity.this, CommandActivity.class);
//            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
