package ensharp.yeey.whisperer.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.DeleteSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.DialogRuntimeResponseGeneric;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v2.model.RuntimeIntent;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import java.util.logging.LogManager;

import ensharp.yeey.whisperer.CommandCenter;
import ensharp.yeey.whisperer.Constant;
import ensharp.yeey.whisperer.R;
import ensharp.yeey.whisperer.WatsonAssistant;

public class CommandActivity extends AppCompatActivity {

    private long touchPressedTime = 0;
    private long resetTime = 2000;

    private SpeechRecognizerClient client;
    private TextToSpeechClient ttsClient;

    static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    // IBM Watson assistant
    private Assistant service;
    private SessionResponse watsonAssistantSession;
    private IamOptions iamOptions;
    private String assistantId;
    private String sessionId;

    public WatsonAssistant watsonAssistant;

    public CommandCenter commandCenter;

    String responseText;
    String commandType;
    String commandDetail;
    String commandSpecificDetail;

    JSONObject result;
    JSONObject extraJsonObject;
    JSONArray extraJsonArray;

    TextView enteringTextView;

    private GestureDetector.SimpleOnGestureListener listener;
    private GestureDetector detector;

    private boolean speechFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        enteringTextView = (TextView)(findViewById(R.id.entering_Voice_TextView));
        enteringTextView.setVisibility(View.INVISIBLE);

        findViewById(R.id.listen_image).bringToFront();
        fadeInOutAnimation();

        int MyVersion = Build.VERSION.SDK_INT;

//        watsonAssistant = new WatsonAssistant();

        //버전 체크
        if (MyVersion >= Build.VERSION_CODES.O) {
            checkVerify();
        } else {
            Toast.makeText(this, "버전이 맞지 않아 이 앱을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        // 초기화
//        InitializeWatsonAssistant();
        InitializeSpeechRecognize();
        InitializeTextToSpeech();

        assistantId = "613a7993-9a45-4c79-86c5-d8a3fc187907";
        result = new JSONObject();
        extraJsonObject = new JSONObject();
        extraJsonArray = new JSONArray();

        createService();


        final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        ttsClient.play("전세영병신");

        listener = new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(!ttsClient.isPlaying() && !speechFlag) {
                    speechFlag = true;
                    enteringTextView.setVisibility(View.VISIBLE);
                    client.startRecording(true);
                    vibrator.vibrate(500);
                }
                return true;
            }
        };
        detector = new GestureDetector(listener);
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

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
//                watsonAssistant.connectWatsonAssistant(texts.get(0));
                Log.e("result", texts.get(0));
                connectWatsonAssistant(texts.get(0));
            }

            @Override
            public void onAudioLevel(float audioLevel) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    // TTS 초기화
    private void InitializeTextToSpeech(){
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());

        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .build();
    }

    public void textToSpeechPlay(String speechText){
        ttsClient.play(speechText);
    }

//    // 터치 연속 2번 감지하는 함수
//    @Override
//    public boolean onTouchEvent (MotionEvent event){
//
//        if (System.currentTimeMillis() > touchPressedTime + resetTime) {
//            touchPressedTime = System.currentTimeMillis();
//        }
//
//        if (System.currentTimeMillis() <= touchPressedTime + resetTime) {
//            // 터치 연속 2번 시 음성 인식 실행
//            enteringTextView.setVisibility(View.VISIBLE);
//            client.startRecording(true);
////            if (responseText == null)
////                ttsClient.play("전세영 쀼유융신");
////            else
////                ttsClient.play(responseText);
//        }
//
//        return super.onTouchEvent(event);
//    }

    // 더 이상 쓰지 않는 경우에는 다음과 같이 해제
    public void onDestroy() {
        super.onDestroy();
//        Log.e("종료", responseText);
//        ttsClient.play();
        SpeechRecognizerManager.getInstance().finalizeLibrary();
//        TextToSpeechManager.getInstance().finalizeLibrary();
    }

    public void onPause() {
        super.onPause();
//        Log.e("멈춤", responseText);
//        ttsClient.play();
    }



    private void ExectueCommand(String command){
        switch(command){
            case Constant.COMMAND_HELPING:
                break;
            case Constant.COMMAND_ALARM:
                break;
            case Constant.COMMAND_BATHROOM:
                Log.e("화장실","화장실로 안내합니다");
                break;
            case Constant.COMMAND_CALL:
                break;
        }
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

    // 어시스턴트 서비스 설정
    private void createService() {
        iamOptions = new IamOptions.Builder().apiKey("Y2Tqfxg5kJg3TSCVPoKbRjY64YBLMGC0PPZQfQvX2Gni").build();
        service = new Assistant("2018-09-20", iamOptions);
    }

    // 세션 삭제, 대화 초기화
    private void deleteService() {
        DeleteSessionOptions deleteSessionOptions = new DeleteSessionOptions.Builder(assistantId, sessionId).build();
        service.deleteSession(deleteSessionOptions).execute();
        watsonAssistantSession = null;
    }

    public void connectWatsonAssistant(final String inputText){
        // 표준 출력에 로그 메시지를 표시하지 않습니다.
        LogManager.getLogManager().reset();

        new Thread(new Runnable() {
            @Override public void run() {
                // 기존의 대화 세션인지 구분, null이면 새로운 대화.
                if (watsonAssistantSession == null) {
                    ServiceCall<SessionResponse> call = service.createSession(new CreateSessionOptions.Builder().assistantId(assistantId).build());
                    watsonAssistantSession = call.execute();
                }

                sessionId = watsonAssistantSession.getSessionId();

                // 대화를 시작하기 위해 빈 값으로 초기화합니다.


                // 어시스턴트로 메시지를 발송합니다.
                MessageInput input = new MessageInput.Builder().text(inputText).build();
                MessageOptions messageOptions = new MessageOptions.Builder(assistantId, sessionId)
                        .input(input)
                        .build();
                MessageResponse response = service.message(messageOptions).execute();


                // 인텐트가 발견된 경우 이를 콘솔에 인쇄합니다.
                List<RuntimeIntent> responseIntents = response.getOutput().getIntents();
                if (responseIntents.size() > 0) {
                    System.out.println("Detected intent: #" + responseIntents.get(0).getIntent());
                }

                // 대화로부터의 출력을 인쇄합니다(있는 경우). 단일 텍스트 응답을 가정합니다.
                List<DialogRuntimeResponseGeneric> responseGeneric = response.getOutput().getGeneric();

                // 단순 대답 응답이 있는 경우
                if (responseGeneric.size() > 0) {
                    System.out.println(response.getOutput().getGeneric().get(0).getText());
                    responseText = response.getOutput().getGeneric().get(0).getText();
                    speechFlag = false;
                    Log.e("response: ", responseText);
                    ttsClient.setSpeechText(responseText);
//                    TTStest();
//                    ttsClient.play(responseText);
//                    Handler mHandler = new Handler(Looper.getMainLooper());
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ttsClient.play(responseText);
//                        }
//                    }, 0);
                    // 안내 응답이 있는 경우
                    if(responseGeneric.size() > 1 && response.getOutput().getGeneric().get(1).getOptions() != null) {
                        commandType= response.getOutput().getGeneric().get(1).getTitle();
                        // 명령어 파싱
                        try {
                            JSONArray jsonArray = new JSONArray(response.getOutput().getGeneric().get(1).getOptions().toString());
                            System.out.println(jsonArray);
                            for(int i = 0 ; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                commandDetail = jsonObject.getString("label");
                                JSONObject jsonObject1 = jsonObject.optJSONObject("value");
                                JSONObject jsonObject2 = jsonObject1.optJSONObject("input");
                                commandSpecificDetail = jsonObject2.optString("text");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        createJSONObject(commandType, commandDetail, commandSpecificDetail);
                        deleteService();
                    }
                }
            }
        }).start();
    }

    public synchronized void TTStest(){
        ttsClient.play();
    }

    public void createJSONObject(String commandType, String commandDetail, String commandSpecificDetail) {
        try {
            result.put("INSTRUCTION", commandType);

            extraJsonObject.put("COMMAND_DESCRIPTION", commandDetail);
            extraJsonObject.put("OPTIONAL_DESCRIPTION", commandSpecificDetail);

            extraJsonArray.put(extraJsonObject);

            result.put("INFORMATION", extraJsonArray);
        } catch (JSONException e) {
            // Do something with the exception
        }
        System.out.println(result);
        Log.e("json: ", responseText);
//        commandActivity.textToSpeechPlay(responseText);
//        InitializeTextToSpeech();


//        ttsClient.play(responseText);
        commandCenter = new CommandCenter(commandType, commandDetail, commandSpecificDetail);
    }

    private void fadeInOutAnimation(){

        final ImageView iv = findViewById(R.id.fade_inout2);

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














