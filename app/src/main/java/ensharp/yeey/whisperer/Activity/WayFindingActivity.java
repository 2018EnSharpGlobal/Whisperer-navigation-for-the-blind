package ensharp.yeey.whisperer.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ensharp.yeey.whisperer.R;

public class WayFindingActivity extends AppCompatActivity {

    ImageView background;
    TextView way_finding_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way_finding);

        background = (ImageView)findViewById(R.id.way_finding_background);
        way_finding_text = (TextView)findViewById(R.id.way_finding_text);

        Glide.with(this).load(R.drawable.way_finding_image).into(background);


    }
}
