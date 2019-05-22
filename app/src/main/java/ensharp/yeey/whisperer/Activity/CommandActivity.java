package ensharp.yeey.whisperer.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
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
import java.util.logging.LogManager;

import ensharp.yeey.whisperer.Constant;
import ensharp.yeey.whisperer.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        int MyVersion = Build.VERSION.SDK_INT;

        //버전 체크
        if (MyVersion >= Build.VERSION_CODES.O) {
            checkVerify();
        } else {
            Toast.makeText(this, "버전이 맞지 않아 이 앱을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        // 초기화
        InitializeWatsonAssistant();
        InitializeSpeechRecognize();
        InitializeTextToSpeech();
    }
    // IBM Watson 변수 초기화
    private void InitializeWatsonAssistant(){
        iamOptions = new IamOptions.Builder().apiKey("Y2Tqfxg5kJg3TSCVPoKbRjY64YBLMGC0PPZQfQvX2Gni").build();
        service = new Assistant("2018-09-20", iamOptions);
        assistantId = "613a7993-9a45-4c79-86c5-d8a3fc187907";
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
                ConnectWatsonAssistant(texts.get(0));
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

    // 터치 연속 2번 감지하는 함수
    @Override
    public boolean onTouchEvent (MotionEvent event){

        if (System.currentTimeMillis() > touchPressedTime + resetTime) {
            touchPressedTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() <= touchPressedTime + resetTime) {
            // 터치 연속 2번 시 음성 인식 실행
            client.startRecording(true);

//            ttsClient.play("박지호 쀼유융신");
        }

        return super.onTouchEvent(event);
    }

    // 더 이상 쓰지 않는 경우에는 다음과 같이 해제
    public void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManager.getInstance().finalizeLibrary();
        TextToSpeechManager.getInstance().finalizeLibrary();
    }

    // IBM Watson Assitant 결과 가져오기 함수
    public void ConnectWatsonAssistant(final String inputText){
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

                // 어시스턴트로 메시지를 발송합니다.
                MessageInput input = new MessageInput.Builder().text(inputText).build();
                MessageOptions messageOptions = new MessageOptions.Builder(assistantId, sessionId).input(input).build();
                MessageResponse response = service.message(messageOptions).execute();

                // 인텐트가 발견된 경우 이를 콘솔에 인쇄합니다.
                List<RuntimeIntent> responseIntents = response.getOutput().getIntents();
                if (responseIntents.size() > 0) {
                    String detectedIntent = responseIntents.get(0).getIntent();
                    ExectueCommand(detectedIntent);
                }

                // 대화로부터의 출력을 인쇄합니다(있는 경우). 단일 텍스트 응답을 가정합니다.
                List<DialogRuntimeResponseGeneric> responseGeneric = response.getOutput().getGeneric();

                // 단순 대답 응답이 있는 경우
                if (responseGeneric.size() > 0) {
                    Log.e("1",response.getOutput().getGeneric().get(0).getText());
                    ttsClient.play(response.getOutput().getGeneric().get(0).getText());

                    // 안내 응답이 있는 경우
                    if(responseGeneric.size() > 1) {
                        String commandType= response.getOutput().getGeneric().get(1).getTitle();
                        String commandDetail = null;
                        String commandSpecificDetail = null;

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

                        Log.e("Command",commandType + " " + commandDetail + " " + commandSpecificDetail);
                        deleteService();
                    }

                }
            }
        }).start();
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

    // 세션 삭제, 대화 초기화
    private void deleteService() {
        DeleteSessionOptions deleteSessionOptions = new DeleteSessionOptions.Builder(assistantId, sessionId).build();
        service.deleteSession(deleteSessionOptions).execute();
        watsonAssistantSession = null;
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














