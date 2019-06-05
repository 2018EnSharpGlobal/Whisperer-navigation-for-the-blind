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

import ensharp.yeey.whisperer.KakaoSTTManager;
import ensharp.yeey.whisperer.R;

public class HelpingActivity extends AppCompatActivity {
    private TextToSpeechClient ttsClient;
    private KakaoSTTManager kakaoSTTManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helping);

        kakaoSTTManager = KakaoSTTManager.getInstance();
        kakaoSTTManager.setContext(this);
        kakaoSTTManager.setActivity(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fadeInOutAnimation();

        kakaoSTTManager.getClient().play("위스퍼러는 크게 4가지 기능으로 구성되어 있습니다." +
                " 첫 번째 길 찾기, 두 번째 지하철 정보 알림, 세 번째 역사무원에게 전화하기, 네 번쨰는 도움말 듣기입니다." +
                "사용자는 명령 입력 화면에서 화면을 두 번 터치를 하고, 음성 명령을 입력할 수 있습니다." +
                "위스퍼러는 명령을 인식 및 분석하여 해당하는 기능을 사용자에게 제공합니다.");
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