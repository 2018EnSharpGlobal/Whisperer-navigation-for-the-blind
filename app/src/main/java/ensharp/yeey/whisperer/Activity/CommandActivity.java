package ensharp.yeey.whisperer.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import java.util.ArrayList;

import ensharp.yeey.whisperer.AnalyzeCommandTask;
import ensharp.yeey.whisperer.Constant;
import ensharp.yeey.whisperer.R;

public class CommandActivity extends AppCompatActivity {

    private long touchPressedTime = 0;
    private long resetTime = 2000;

    private SpeechRecognizerClient client;

    static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    ImageView background;

    String input;

    TextToSpeechClient ttsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        background = (ImageView)findViewById(R.id.command_background);

        Glide.with(this).load(R.drawable.command_waiting_image).into(background);

        int MyVersion = Build.VERSION.SDK_INT;

        //버전 체크
        if (MyVersion >= Build.VERSION_CODES.O) {
            checkVerify();
        } else {
            Toast.makeText(this, "버전이 맞지 않아 이 앱을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        InitializeSpeechRecognize();

        TextToSpeechManager.getInstance().initializeLibrary(this);

        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)
                .setSpeechSpeed(1.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)
                .setListener(new TextToSpeechListener() {
                    @Override
                    public void onFinished() {
                        Log.e("출력","해라");
                    }

                    @Override
                    public void onError(int code, String message) {

                    }
                })
                .build();

    }

    // STT 초기화
    private void InitializeSpeechRecognize(){
        // SDK 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        // 클라이언트 생성
        SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB);
        client = builder.build();

        client.setSpeechRecognizeListener(new SpeechRecognizeListener() {
            @Override
            public void onReady() {
                Log.e("준비", "준비");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("시작", "시작");
            }

            @Override
            public void onEndOfSpeech() {
                Log.e("끝", "끝");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e("오류", errorMsg);
            }

            @Override
            public void onPartialResult(String partialResult) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> texts =  results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
                input = texts.get(0);
                Log.e("result", texts.get(0));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AnalyzeCommandTask(getApplicationContext(), input).execute();
                    }
                });
            }

            @Override
            public void onAudioLevel(float audioLevel) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    // 터치 연속 2번 감지하는 함수
    @Override
    public boolean onTouchEvent (MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(System.currentTimeMillis() > touchPressedTime + 1500){
                    touchPressedTime = System.currentTimeMillis();
                    return false;
                }
                if(System.currentTimeMillis() <= touchPressedTime + 1500){

                    client.startRecording(true);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    // 더 이상 쓰지 않는 경우에는 다음과 같이 해제
    public void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }

    //권학 확인하는 함수
    private void checkVerify()
    {
        //권한
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        //권한 확인
        if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //권한 여러개 중 하나라도 허용 못했다면?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CHANGE_WIFI_STATE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_WIFI_STATE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                //권한이 왜 필요한지 설명
                // ...
            }
            requestPermissions(neededPermissions,Constant.PERMISSION_REQUEST_CODE);
        }
        else
        //권한 여러개 모두 허용 했다면?
        {
            Toast.makeText(this,"모두 허용",Toast.LENGTH_LONG).show();
        }
    }

    //권한 요청 작업에 대한 CallBack 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
            }
        }
    }
}














