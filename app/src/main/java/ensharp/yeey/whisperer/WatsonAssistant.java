package ensharp.yeey.whisperer;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
//import android.os.Handler;

import com.google.gson.JsonObject;
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
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.LogManager;
import ensharp.yeey.whisperer.Common.VO.AnalyzeVO;


public class WatsonAssistant {

    private Assistant service;
    private SessionResponse watsonAssistantSession;
    private IamOptions iamOptions;
    private String assistantId;
    private String sessionId;

    public String responseText;
    public CommandCenter commandCenter;


    JSONObject result;
    JSONObject extraJsonObject;
    JSONArray extraJsonArray;
    String commandType;
    String commandDetail;
    String commandSpecificDetail;

    Context context;
    AnalyzeVO analyzeVO;

    String inputText;


//    createService();
//        InitializeTextToSpeech();
//        ttsClient.play("전세영 시발");

//    public WatsonAssistant(Context context) {
//        this.context = context;
//        assistantId = "613a7993-9a45-4c79-86c5-d8a3fc187907";
//        result = new JSONObject();
//        extraJsonObject = new JSONObject();
//        extraJsonArray = new JSONArray();
//        CreateService();
//}


    public WatsonAssistant(){
        if(service == null)
            CreateService();
    }
//
//    // TTS 초기화
//    private void InitializeTextToSpeech(){
//        TextToSpeechManager.getInstance().initializeLibrary(mContext);
//
//        ttsClient = new TextToSpeechClient.Builder()
//                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
//                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
//                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
//                .build();
//        ttsClient.play("전세영 시발");
//    }

    // Watson Assistant 변수 설정
    private void CreateService() {
        assistantId = Constant.ASSISTANT_ID;
        iamOptions = new IamOptions.Builder().apiKey(Constant.WATSON_ASSISTANT_API_KEY).build();
        service = new Assistant(Constant.VERSION_DATE, iamOptions);
    }

    // 세션 삭제, 대화 초기화
    private void DeleteService() {
        DeleteSessionOptions deleteSessionOptions = new DeleteSessionOptions.Builder(assistantId, sessionId).build();
        service.deleteSession(deleteSessionOptions).execute();
        watsonAssistantSession = null;
    }

    public void AnalyzeResult(final String inputText) {
        analyzeVO = new AnalyzeVO();
        this.inputText = inputText;

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (watsonAssistantSession == null) {
                    ServiceCall<SessionResponse> call = service.createSession(new CreateSessionOptions.Builder().assistantId(assistantId).build());
                    watsonAssistantSession = call.execute();
                }
                sessionId = watsonAssistantSession.getSessionId();

                MessageInput input = new MessageInput.Builder().messageType("text").text(inputText).build();
                MessageOptions options = new MessageOptions.Builder(assistantId, sessionId).input(input).build();

                MessageResponse response = service.message(options).execute();

                List<RuntimeIntent> responseIntents = response.getOutput().getIntents();
                if (responseIntents.size() > 0) {
                    Log.e("Detected intent: #", responseIntents.get(0).getIntent());
                }

                List<DialogRuntimeResponseGeneric> responseGeneric = response.getOutput().getGeneric();

                // 단순 대답 응답이 있는 경우
                if (responseGeneric.size() > 0) {
                    Log.e("Reulst1", response.getOutput().getGeneric().get(0).toString());

                    analyzeVO.setLabel(response.getOutput().getGeneric().get(0).getDescription());
                    analyzeVO.setValue(response.getOutput().getGeneric().get(0).getMessageToHumanAgent());
                    analyzeVO.setInput(response.getOutput().getGeneric().get(0).getPreference());
                    analyzeVO.setText(response.getOutput().getGeneric().get(0).getText());

                    // 안내 응답이 있는 경우
                    if (responseGeneric.size() > 1) {
                        // 명령어 파싱
                        try {
                            JSONArray jsonArray = new JSONArray(response.getOutput().getGeneric().get(1).getOptions().toString());
                            System.out.println(jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                analyzeVO.setLabel(jsonObject.getString("label"));
                                analyzeVO.setValue(jsonObject.getString("value"));
                                analyzeVO.setInput(jsonObject.getString("input"));
                                analyzeVO.setText(jsonObject.getString("text"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        DeleteService();

                        AnalyzeCommandTask analyzeCommandTask = new AnalyzeCommandTask();
                        analyzeCommandTask.GetObject(analyzeVO);

                    }
                }
            }
        }).start();
    }
//
//    public void connectWatsonAssistant(final String inputText){
//        // 표준 출력에 로그 메시지를 표시하지 않습니다.
//        LogManager.getLogManager().reset();
//
//        new Thread(new Runnable() {
//            @Override public void run() {
//                // 기존의 대화 세션인지 구분, null이면 새로운 대화.
//                if (watsonAssistantSession == null) {
//                    ServiceCall<SessionResponse> call = service.createSession(new CreateSessionOptions.Builder().assistantId(assistantId).build());
//                    watsonAssistantSession = call.execute();
//                }
//
//                sessionId = watsonAssistantSession.getSessionId();
//
//                // 대화를 시작하기 위해 빈 값으로 초기화합니다.
//
//
//                // 어시스턴트로 메시지를 발송합니다.
//                MessageInput input = new MessageInput.Builder().text(inputText).build();
//                MessageOptions messageOptions = new MessageOptions.Builder(assistantId, sessionId)
//                        .input(input)
//                        .build();
//                MessageResponse response = service.message(messageOptions).execute();
//
//
//                // 인텐트가 발견된 경우 이를 콘솔에 인쇄합니다.
//                List<RuntimeIntent> responseIntents = response.getOutput().getIntents();
//                if (responseIntents.size() > 0) {
//                    System.out.println("Detected intent: #" + responseIntents.get(0).getIntent());
//                }
//
//                // 대화로부터의 출력을 인쇄합니다(있는 경우). 단일 텍스트 응답을 가정합니다.
//                List<DialogRuntimeResponseGeneric> responseGeneric = response.getOutput().getGeneric();
//
//                // 단순 대답 응답이 있는 경우
//                if (responseGeneric.size() > 0) {
//                    System.out.println(response.getOutput().getGeneric().get(0).getText());
//                    responseText = response.getOutput().getGeneric().get(0).getText();
//                    Log.e("response: ", responseText);
////                    ttsClient.play(responseText);
////                    Handler mHandler = new Handler(Looper.getMainLooper());
////                    mHandler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////
////                        }
////                    }, 0);
//                    // 안내 응답이 있는 경우
//                    if(responseGeneric.size() > 1) {
//                        commandType= response.getOutput().getGeneric().get(1).getTitle();
//                        // 명령어 파싱
//                        try {
//                            JSONArray jsonArray = new JSONArray(response.getOutput().getGeneric().get(1).getOptions().toString());
//                            System.out.println(jsonArray);
//                            for(int i = 0 ; i<jsonArray.length(); i++){
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                commandDetail = jsonObject.getString("label");
//                                JSONObject jsonObject1 = jsonObject.optJSONObject("value");
//                                JSONObject jsonObject2 = jsonObject1.optJSONObject("input");
//                                commandSpecificDetail = jsonObject2.optString("text");
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
////                        createJSONObject(commandType, commandDetail, commandSpecificDetail);
////                        deleteService();
//
////                    responseText = response.getOutput().getGeneric().get(0).getText();
//
//
//                    // 안내 응답이 있는 경우
//                    if(responseGeneric.size() > 1) {
////                        commandType= response.getOutput().getGeneric().get(1).getTitle();
//////                        textToSpeech();
////                        // 명령어 파싱
////                        try {
////                            JSONArray jsonArray = new JSONArray(response.getOutput().getGeneric().get(1).getOptions().toString());
////                            System.out.println(jsonArray);
////                            for(int i = 0 ; i<jsonArray.length(); i++){
////                                JSONObject jsonObject = jsonArray.getJSONObject(i);
////                                commandDetail = jsonObject.getString("label");
////                                JSONObject jsonObject1 = jsonObject.optJSONObject("value");
////                                JSONObject jsonObject2 = jsonObject1.optJSONObject("input");
////                                commandSpecificDetail = jsonObject2.optString("text");
////                            }
////
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
////
////                        createJSONObject(commandType, commandDetail, commandSpecificDetail);
////                        DeleteService();
//
//                    }
//                }
//            }
//        }).start();
//    }

//    public void createJSONObject(String commandType, String commandDetail, String commandSpecificDetail) {
//        try {
//            result.put("INSTRUCTION", commandType);
//
//            extraJsonObject.put("COMMAND_DESCRIPTION", commandDetail);
//            extraJsonObject.put("OPTIONAL_DESCRIPTION", commandSpecificDetail);
//
//            extraJsonArray.put(extraJsonObject);
//
//            result.put("INFORMATION", extraJsonArray);
//        } catch (JSONException e) {
//            // Do something with the exception
//        }
//        System.out.println(result);
//        commandCenter = new CommandCenter(commandType, commandDetail, commandSpecificDetail);
//    }

}
