package ensharp.yeey.whisperer;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CommandWaitingActivity extends AppCompatActivity {

    private Intent i;
    private SpeechRecognizer mRecognizer;
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.e("Speech","Ready for the Speech");
        }

        @Override
        public void onBeginningOfSpeech() {
//            Toast.makeText(this,"지금부터 말씀해주세요",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle bundle) {

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    //터치이벤트 관련 변수
    int clickCount = 0;
    long startTime;
    long duration;

    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    clickCount++;
                    break;
                case MotionEvent.ACTION_UP:
                    long time = System.currentTimeMillis() - startTime;
                    duration = duration + time;
                    if(clickCount == 2){
                        //더블 클릭
                        if( duration <= Constant.MAX_DURATION){

                        }
                        clickCount = 0;
                        duration = 0;
                        break;
                    }
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_waiting);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

    }



}
