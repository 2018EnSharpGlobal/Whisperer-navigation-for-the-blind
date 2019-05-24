package ensharp.yeey.whisperer;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CommandWaitingActivity extends AppCompatActivity {
    private SpeechRecognizerClient client;

    private long touchPressedTime = 0;
    private long resetTime = 2000;
    String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_waiting);

        try {
            PackageInfo info = getPackageManager().getPackageInfo("ensharp.yeey.whisperer", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        if (CheckPermission(Manifest.permission.RECORD_AUDIO) || CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                CheckPermission(Manifest.permission.CHANGE_WIFI_STATE) || CheckPermission(Manifest.permission.ACCESS_WIFI_STATE) ||
                CheckPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || CheckPermission(Manifest.permission.CALL_PHONE)){
            ActivityCompat.requestPermissions(CommandWaitingActivity.this,new String[]{Manifest.permission.CALL_PHONE,//전화거는 퍼미션
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, //기록받아오는 퍼미션
                    Manifest.permission.RECORD_AUDIO, //오디오기록 퍼미션
                    Manifest.permission.ACCESS_WIFI_STATE, //전화 상태를 읽을 수 있는 퍼미션
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },0);
//            //권한 여러개 중 하나라도 허용 못했다면?
//            if (ShowRequestPermission(Manifest.permission.CHANGE_WIFI_STATE) || ShowRequestPermission(Manifest.permission.ACCESS_WIFI_STATE) ||
//                    ShowRequestPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || ShowRequestPermission(Manifest.permission.CALL_PHONE) ||
//                    ShowRequestPermission(Manifest.permission.RECORD_AUDIO) || ShowRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//
//            }
        }

        // SDK 초기화
        SpeechRecognizerManager.getInstance().initializeLibrary(this);

        // 클라이언트 생성
        SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                setGlobalTimeOut(5).
                setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WORD).
                setUserDictionary("찐따");

        client = builder.build();


        client.setSpeechRecognizeListener(new SpeechRecognizeListener() {
            @Override
            public void onReady() {
                Log.e("음성인식", "준비");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("음성인식", "시작");
            }

            @Override
            public void onEndOfSpeech() {
                Log.e("음성인식", "끝");
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Log.e("음성인식", "에러");
            }

            @Override
            public void onPartialResult(String partialResult) {
                Log.e("인식", partialResult);
            }

            @Override
            public void onResults(Bundle results) {
                final StringBuilder builder = new StringBuilder();
                Log.e("Speech","Result");

                ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
                ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);

                for(int i = 0 ; i <texts.size() ; i++){
                    builder.append(texts.get(i));
                    builder.append(" (");
                    builder.append(confs.get(i).intValue());
                    builder.append(")\n");
                }

                final Activity activity = CommandWaitingActivity.this;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // finishing일때는 처리하지 않는다.
                        if (activity.isFinishing()) return;

                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity).
                                setMessage(builder.toString()).
                                setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();

//                        setButtonsStatus(true);
                    }
                });

                client = null;
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
    public boolean onTouchEvent(MotionEvent event) {

        if (System.currentTimeMillis() > touchPressedTime + resetTime ) {
            touchPressedTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() <= touchPressedTime + resetTime ) {
            // 터치 연속 2번 시 음성 인식 실행
            if(PermissionUtils.checkAudioRecordPermission(this)) {

                SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                        setServiceType(serviceType);

                if (serviceType.equals(SpeechRecognizerClient.SERVICE_TYPE_WORD)) {
////                    EditText words = (EditText)findViewById(R.id.words_edit);
//                    String wordList = words.getText().toString();
////                    builder.setUserDictionary(wordList);
//
//                    Log.i("SpeechSampleActivity", "word list : " + wordList.replace('\n', ','));
                }

                client = builder.build();

//                client.setSpeechRecognizeListener(this);
                client.startRecording(true);
                Log.e("음성인식", "시작하자");
            }
        }

        return super.onTouchEvent(event);
    }

    public void onDestroy(){
        super.onDestroy();

        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }

    // 권한 체크하는 함수
    private boolean CheckPermission(String permission){
        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean ShowRequestPermission(String permission){
        if(shouldShowRequestPermissionRationale(permission)){
            return true;
        } else{
            return false;
        }
    }
}
