package ensharp.yeey.whisperer;

import android.content.Context;
import android.util.Log;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

//import ensharp.yeey.whisperer.Common.VO.PathVO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

class ODsayServiceManager {
    private static final ODsayServiceManager ourInstance = new ODsayServiceManager();

    static ODsayServiceManager getInstance() {
        return ourInstance;
    }

    private Context context;
    private MainActivity mainActivity;

    private ODsayService odsayService;
    private JSONObject jsonObject;

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
                case "지하철 경로검색 조회(지하철 노선도)":
//                    parsePath(jsonObject);
                    break;
                case "반경내 대중교통 POI 검색":
                   // if (api == API.POINT_SEARCH) {
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
     * @param start 출발역 코드
     * @param end 도착역 코드
     */
    public void calculatePath(String start, String end) {
        odsayService.requestSubwayPath("1000", start, end, "2", onResultCallbackListener);
    }

//    public ArrayList<PathVO> parsePath(JSONObject jsonObject) {
//        try {
//            JSONArray jsonArray = jsonObject.getJSONArray("result");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                HashMap map = new HashMap<>();
//                JSONObject object = jsonArray.getJSONObject(i);
//
//                // to be continued
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

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