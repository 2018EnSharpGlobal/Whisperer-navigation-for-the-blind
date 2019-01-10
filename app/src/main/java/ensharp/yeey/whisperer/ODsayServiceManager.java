package ensharp.yeey.whisperer;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import ensharp.yeey.whisperer.Common.VO.ExchangeInfoVO;
import ensharp.yeey.whisperer.Common.VO.PathVO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

class ODsayServiceManager {
    private static final ODsayServiceManager ourInstance = new ODsayServiceManager();

    static ODsayServiceManager getInstance() {
        return ourInstance;
    }

    private MainActivity mainActivity;

    private ODsayService odsayService;
    private JSONObject jsonObject;

    private PathVO path;

    private static String TAG = "API Callback";

    private ODsayServiceManager() {
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * 지하철 운행정보를 가져오는 API를 호출합니다.
     * ODsayService 객체는 싱글톤으로 생성됩니다.
     */
    public void initAPI() {
        odsayService = ODsayService.init(mainActivity, mainActivity.getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
    }

    /**
     * API가 호출된 후 실행되는 콜백 메소드입니다.
     * 호출 결과를 로그값으로 나타냅니다.
     */
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();

            switch (api.name()) {
                case "SUBWAY_PATH":
                    path = parsePath(jsonObject);
                    break;
                case "POINT_SEARCH":
                        JSONObject result = null;
                        try {
                            result = oDsayData.getJson().getJSONObject("result");
                            JSONArray station_array = result.getJSONArray("station");
                            JSONObject station = station_array.getJSONObject(5);

                            //필요한 station_Id 정보
                            String station_ID = station.getString("stationID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    break;
            }

            Log.e(TAG, "onSuccess: " + jsonObject.toString());
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.e(TAG, "onError: API : " + api.name() + "\n" + errorMessage);
        }
    };

    //가까운 지하철역 코드 조회
    public void find_closer_station_code(double latitude, double longitude){
        odsayService.requestPointSearch(String.valueOf(longitude), String.valueOf(latitude), "5000", "2", onResultCallbackListener);
    }

    /**
     * 출발역과 도착역의 코드를 파라미터로 전달하면 이동 경로를 계산합니다.
     * 계산된 이동 경로는 path에 저장됩니다.
     * @param start 출발역 코드
     * @param end 도착역 코드
     */
    public void calculatePath(String start, String end) {
        odsayService.requestSubwayPath("1000", start, end, "2", onResultCallbackListener);
    }

    /**
     * 지하철 경로를 파싱하는 메소드입니다.
     * @param jsonObject API에서 반환된 JSONObject
     * @return 파싱된 PathVO 객체
     */
    public PathVO parsePath(JSONObject jsonObject) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PathVO.class, new RestDeserializer<>(PathVO.class, "result"))
                .create();
        PathVO path = gson.fromJson(jsonObject.toString(), PathVO.class);

        if (path.getExChangeInfoSet() == null)
            return path;

        path.setExchangeInfoList(parseExchangeInfo(path.getExChangeInfoSet()));

        return path;
    }

    /**
     * 지하철 환승 정보를 파싱하는 메소드입니다.
     * @param jsonObject 환승 정보를 담고있는 jsonObject
     * @return 파싱된 ExchangeInfoVO List
     */
    public List<ExchangeInfoVO> parseExchangeInfo(JsonElement jsonObject) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(jsonObject.toString())
                .getAsJsonObject().get("exChangeInfo");

        Type listType = new TypeToken<List<ExchangeInfoVO>>() {}.getType();
        List<ExchangeInfoVO> exchangeInfoList = (List<ExchangeInfoVO>) gson.fromJson(rootElement, listType);

        return exchangeInfoList;
    }

    /**
     * 지하철역의 이름을 입력하면 해당 역의 코드를 반환하는 메소드입니다.
     * @param station 역명
     * @return 지하철역 코드
     * String station_code = excelManager.Find_Data(station, Constant.STATION_NAME, Constant.STATION_CODE);
     */
    public String getStationCode(String station) {
        InputStream inputStream = null;
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            inputStream = mainActivity.getAssets().open("station_data.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);

            int rowStart = 1;
            int rowEnd = sheet.getColumn(0).length;
            int nameColumn = 1;
            int codeColumn = 2;

            for(int row = rowStart; row <= rowEnd; row++) {
                String name = sheet.getCell(nameColumn, row).getContents();
                if(name.equals(station)){
                    return sheet.getCell(codeColumn, row).getContents();
                }
            }

            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (BiffException e) {
            e.printStackTrace();
            return "";
        } finally {
            workbook.close();
        }
    }

    public PathVO getPath() {
        return path;
    }

    /**
     * JSON Object를 Deserialzer해줍니다.
     * 모든 VO class를 사용하기 위해 제네릭 형식을 사용하고, 키값을 받아오도록 합니다.
     * @param <T> 파싱할 VO class
     */
    private class RestDeserializer<T> implements JsonDeserializer<T> {
        private Class<T> usingClass;
        private String key;

        public RestDeserializer(Class<T> targetClass, String key) {
            usingClass = targetClass;
            this.key = key;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement content = json.getAsJsonObject().get(key);
            return new Gson().fromJson(content, usingClass);
        }
    }
}
