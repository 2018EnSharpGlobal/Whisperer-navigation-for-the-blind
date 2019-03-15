package ensharp.yeey.whisperer;

import android.util.Log;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import ensharp.yeey.whisperer.Common.ParseManager;
import ensharp.yeey.whisperer.Common.VO.BusStopVO;
import ensharp.yeey.whisperer.Common.VO.BusVO;
import ensharp.yeey.whisperer.Common.VO.DefaultInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExchangeInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExitInfoVO;
import ensharp.yeey.whisperer.Common.VO.PathVO;
import ensharp.yeey.whisperer.Common.VO.SubwayStationInfoVO;
import ensharp.yeey.whisperer.Common.VO.SubwayTimeTableVO;
import ensharp.yeey.whisperer.Common.VO.UseInfoVO;
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
    private ParseManager parseManager;

    private PathVO path;
    private SubwayStationInfoVO station;
    private SubwayTimeTableVO timeTable;

    private String wayCode;

    private static String TAG = "API Callback";

    private ODsayServiceManager() {
        parseManager = ParseManager.getInstance();
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
                case "SUBWAY_PATH": // 지하철 경로 검색
                    path = parseManager.parsePath(jsonObject);
                    // path 이용 메소드 올 곳
                    ((TextView)mainActivity.findViewById(R.id.result)).setText(path.toString());
                    break;
                case "POINT_SEARCH":    // 반경내 대중교통 POI 검색, thread 처리해야 할 것 같음
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
                case "SUBWAY_STATION_INFO": // 지하철역 세부 정보
                    station = parseManager.parseStation(jsonObject);
                    // station 이용 메소드 올 곳
                    ((TextView)mainActivity.findViewById(R.id.result)).setText(station.toString());
                        break;
                case "SUBWAY_TIME_TABLE":
                    timeTable = parseManager.parseTimeTable(jsonObject, wayCode);
                    // timeTable 이용 메소드 올 곳
                    ((TextView)mainActivity.findViewById(R.id.result)).setText(timeTable.toString());
                    break;
                default:
                    Log.e(TAG, "api 이름: " + api.name());
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
     * requestSubwayPath는 비동기로 진행됩니다.
     * 계산된 이동 경로는 path에 저장됩니다.
     * @param start 출발역 코드
     * @param end 도착역 코드
     */
    public void calculatePath(String start, String end) {
        odsayService.requestSubwayPath("1000", start, end, "2", onResultCallbackListener);
    }

    /**
     * 지하철역의 세부 정보를 가져오는 메소드입니다.
     * 정보는 station에 저장됩니다.
     * @param station 지하철역 코드
     */
    public void getSubwayInfo(String station) {
        odsayService.requestSubwayStationInfo(station, onResultCallbackListener);
    }

    /**
     * 지하철역의 시간표를 가져오는 메소드입니다.
     * 정보는 timeTable에 저장됩니다.
     * @param station 지하철역 코드
     * @param wayCode 상행/하행 여부
     */
    public void getSubwayTimeTable(String station, String wayCode) {
        this.wayCode = wayCode;
        odsayService.requestSubwayTimeTable(station, wayCode, onResultCallbackListener);
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
}
