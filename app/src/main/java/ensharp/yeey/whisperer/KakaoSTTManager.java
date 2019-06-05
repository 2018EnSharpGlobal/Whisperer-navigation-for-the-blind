package ensharp.yeey.whisperer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

public class KakaoSTTManager {
    private static final KakaoSTTManager ourInstance = new KakaoSTTManager();

    public static KakaoSTTManager getInstance() {
        return ourInstance;
    }

    private TextToSpeechClient ttsClient;
    private Context context;
    private Activity activity;

    KakaoSTTManager(){

    }

    public void setContext(Context _context){
        this.context = _context;
        InitializeTextToSpeech();
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    // TTS 초기화
    private void InitializeTextToSpeech(){
        TextToSpeechManager.getInstance().initializeLibrary(context);
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

                activity.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                activity.finish();
            }

            @Override
            public void onError(int code, String message) {
                handleError(code);
            }
        }).build();

        // audio 출력 최대
        AudioManager audio = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 5, AudioManager.FLAG_PLAY_SOUND);
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

    public TextToSpeechClient getClient(){
        return ttsClient;
    }
}
