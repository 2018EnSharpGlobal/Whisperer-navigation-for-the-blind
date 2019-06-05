package ensharp.yeey.whisperer.Activity;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import ensharp.yeey.whisperer.Constant;
import ensharp.yeey.whisperer.ExcelManager;
import ensharp.yeey.whisperer.ODsayServiceManager;
import ensharp.yeey.whisperer.R;

public class InformationActivity extends AppCompatActivity {
    private ODsayServiceManager oDsayServiceManager;

    String instruction;
    String departure;
    String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent intent = getIntent();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fadeInOutAnimation();
        initSubwayAPI();
        oDsayServiceManager.setSTTContext(this.getApplicationContext());
        oDsayServiceManager.setActivity(this);

        instruction = intent.getStringExtra("instruction");
        departure = intent.getStringExtra("departure");
        destination = intent.getStringExtra("destination");

        ExectueInformation();
    }

    private void initSubwayAPI() {
        oDsayServiceManager = ODsayServiceManager.getInstance();
        oDsayServiceManager.initAPI(this);
    }

    private void ExectueInformation() {
        switch (instruction) {
            case Constant.COMMAND_ROUTE:
                oDsayServiceManager.calculatePath(departure,destination);
                break;
            case Constant.COMMAND_TIME:
                oDsayServiceManager.getSubwayTimeTable(departure,GetDirectionStation(departure,destination));
                break;
            case Constant.COMMAND_CALL:
                oDsayServiceManager.findCloserStationCode(126.933361407195,37.3643392278118);
                break;
        }
    }

    private String GetDirectionStation(String departure,String destination){
        ExcelManager excelManager = new ExcelManager(this);
        String departure_code = excelManager.Find_Data(departure,Constant.STATION_NAME, Constant.STATION_CODE);
        String destination_code = excelManager.Find_Data(destination,Constant.STATION_NAME, Constant.STATION_CODE);
        if(Integer.parseInt(departure_code) < Integer.parseInt(destination_code)){
            return "1";
        }
        else{
            return "0";
        }
    }

    private void fadeInOutAnimation(){

        final ImageView iv = findViewById(R.id.fade_inout3);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setRepeatCount(Animation.INFINITE);
        fadeIn.setRepeatMode(Animation.REVERSE);
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setRepeatCount(Animation.INFINITE);
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.setRepeatCount(Animation.INFINITE);

        iv.setAnimation(animation);
    }
}
