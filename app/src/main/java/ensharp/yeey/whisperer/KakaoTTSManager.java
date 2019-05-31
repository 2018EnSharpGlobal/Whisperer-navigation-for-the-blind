package ensharp.yeey.whisperer;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import ensharp.yeey.whisperer.Common.ParseManager;

public class KakaoTTSManager {
    private static final KakaoTTSManager ourInstance = new KakaoTTSManager();

    public static KakaoTTSManager getInstance() {
        return ourInstance;
    }

    private TextToSpeechClient ttsClient;

    private KakaoTTSManager() {

    }

    public void InitTTSClient(Context context){
        TextToSpeechManager.getInstance().initializeLibrary(context);
        SetTTSClient(context);
    }

    public TextToSpeechClient GetTTSClient() {
        return ttsClient;
    }

    private void SetTTSClient(Context context){
        if(ttsClient != null && ttsClient.isPlaying()){
            ttsClient.stop();
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

                ttsClient = null;
            }

            @Override
            public void onError(int code, String message) {
                handleError(code);

                ttsClient = null;
            }
        }).build();

        AudioManager audio = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
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

    public void PlayTextToSpeech(String input){
        if(ttsClient.play(input)){
            Log.e("현재","음성출력");
        }
    }


}
