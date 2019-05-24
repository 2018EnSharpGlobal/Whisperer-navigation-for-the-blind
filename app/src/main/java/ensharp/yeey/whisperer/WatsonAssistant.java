package ensharp.yeey.whisperer;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.logging.LogManager;

public class WatsonAssistant{

    private Assistant service;
    private SessionResponse watsonAssistantSession;
    private IamOptions iamOptions;
    private String assistantId;
    private String sessionId;

    public CommandCenter commandCenter;

    String responseText;
    String commandType;
    String commandDetail;
    String commandSpecificDetail;

    JSONObject result;
    JSONObject extraJsonObject;
    JSONArray extraJsonArray;

    public WatsonAssistant(){
        assistantId = "613a7993-9a45-4c79-86c5-d8a3fc187907";
        result = new JSONObject();
        extraJsonObject = new JSONObject();
        extraJsonArray = new JSONArray();

        createService();
    }

//
//    // 테스트하는 임시 명령어들, 순서대로 명령어 변경
//    @Override
//    public void onClick(View v){
//        // 임시 명령어
//        if(testNum == 0){
//            testNum += 1;
//            testString = "화장실 갈래";
//            connectWatsonAssistant();
//        } else if(testNum == 1){
//            testNum += 1;
//            testString = "남자";
//            connectWatsonAssistant();
//        } else if(testNum == 2){
//            testNum += 1;
//            testString = "화장실 갈래";
//            connectWatsonAssistant();
//        } else {
//            testString = "남자";
//            connectWatsonAssistant();
//        }
//    }

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

                    // 안내 응답이 있는 경우
                    if(responseGeneric.size() > 1) {
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

//                        System.out.println(commandType + " " + commandDetail + " " + commandSpecificDetail);
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
        commandCenter = new CommandCenter(commandType, commandDetail, commandSpecificDetail);
    }
}
