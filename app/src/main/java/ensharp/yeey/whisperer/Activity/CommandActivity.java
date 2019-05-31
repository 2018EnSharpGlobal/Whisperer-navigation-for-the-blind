package ensharp.yeey.whisperer.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import ensharp.yeey.whisperer.CommandCenter;
import ensharp.yeey.whisperer.Constant;
import ensharp.yeey.whisperer.R;

public class CommandActivity extends AppCompatActivity {

    private SpeechRecognizerClient client;
    private TextToSpeechClient ttsClient;

    // IBM Watson assistant
    private Assistant service;
    private SessionResponse watsonAssistantSession;
    private IamOptions iamOptions;
    private String assistantId;
    private String sessionId;

    public CommandCenter commandCenter;

    String commandType;
    String commandDetail;
    String commandSpecificDetail;
    String responseText;

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

        // 음성 입력중 textView
        enteringTextView = (TextView)(findViewById(R.id.entering_Voice_TextView));
        enteringTextView.setVisibility(View.INVISIBLE);

        findViewById(R.id.listen_image).bringToFront();

        // 뾰로롱 애니메이션
        fadeInOutAnimation();

        int MyVersion = Build.VERSION.SDK_INT;

        //버전 체크
        if (MyVersion >= Build.VERSION_CODES.O) {
            checkVerify();
        } else {
            Toast.makeText(this, "버전이 맞지 않아 이 앱을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        // 초기화
        InitializeSpeechRecognize();
        InitializeTextToSpeech();

        assistantId = "613a7993-9a45-4c79-86c5-d8a3fc187907";
        result = new JSONObject();
        extraJsonObject = new JSONObject();
        extraJsonArray = new JSONArray();

        createService();

        final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

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
                Log.e("result", texts.get(0));
                connectWatsonAssistant(texts.get(0));
                enteringTextView.setVisibility(View.INVISIBLE);
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
        TextToSpeechManager.getInstance().initializeLibrary(this);
        if(ttsClient != null && ttsClient.isPlaying()){
            ttsClient.stop();
            Log.e("지움", "지움");
            return;
        }

        ttsClient = new TextToSpeechClient.Builder().setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1).setSpeechSpeed(1.0).
                setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM).setListener(new TextToSpeechListener() {
            @Override
            public void onFinished() {
                int intSentSize = ttsClient.getSentDataSize();
                int intRecvSize = ttsClient.getReceivedDataSize();

                final String strInacctiveText = "onFinished() SentSize : " + intSentSize + " RecvSize : " + intRecvSize;

                Log.e("finished", strInacctiveText);
            }

            @Override
            public void onError(int code, String message) {
                handleError(code);
            }
        }).build();

        // audio 출력 최대
        AudioManager audio = (AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    private void handleError(int errorCode) {
        String errorText;
        switch (errorCode) {
            case TextToSpeechClient.ERROR_NETWORK:
                errorText = "네트워크 오류";
                break;
            case TextToSpeechClient.ERROR_NETWORK_TIMEOUT:
                errorText = "네트워크 지연";
                break;
            case TextToSpeechClient.ERROR_CLIENT_INETRNAL:
                errorText = "음성합성 클라이언트 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_INTERNAL:
                errorText = "음성합성 서버 내부 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_TIMEOUT:
                errorText = "음성합성 서버 최대 접속시간 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_AUTHENTICATION:
                errorText = "음성합성 인증 실패";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_BAD:
                errorText = "음성합성 텍스트 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_SPEECH_TEXT_EXCESS:
                errorText = "음성합성 텍스트 허용 길이 초과";
                break;
            case TextToSpeechClient.ERROR_SERVER_UNSUPPORTED_SERVICE:
                errorText = "음성합성 서비스 모드 오류";
                break;
            case TextToSpeechClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS:
                errorText = "허용 횟수 초과";
                break;
            default:
                errorText = "정의하지 않은 오류";
                break;
        }

        final String statusMessage = errorText + " (" + errorCode + ")";

        Log.e("Error", statusMessage);
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
                    AudioManager audio = (AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                    // tts 출력
                    ttsClient.play(responseText);
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

