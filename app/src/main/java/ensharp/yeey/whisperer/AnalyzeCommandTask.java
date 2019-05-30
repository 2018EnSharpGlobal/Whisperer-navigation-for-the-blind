package ensharp.yeey.whisperer;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.DeleteSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import org.json.JSONArray;
import org.json.JSONObject;

import ensharp.yeey.whisperer.Common.VO.AnalyzeVO;

public class AnalyzeCommandTask extends AsyncTask {

    private WatsonAssistant watsonAssistant;
    private Context context;
    private KakaoTTSManager kakaoTTSManager;
    private String inputText;
    private String resultText;

    public AnalyzeCommandTask(Context context,String inputText) {

        this.context = context;
        this.inputText = inputText;

        kakaoTTSManager = KakaoTTSManager.getInstance();
        kakaoTTSManager.InitTTSClient(context);

        watsonAssistant = new WatsonAssistant();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        AnalyzeVO analyzeVO = watsonAssistant.AnalyzeResult(inputText);

        resultText = analyzeVO.getText();

        Log.e("텍스트",analyzeVO.getText().toString());

        return analyzeVO.getText();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        kakaoTTSManager.PlayTextToSpeech(resultText);
    }
}
