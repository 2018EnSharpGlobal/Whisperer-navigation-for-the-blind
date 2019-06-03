package ensharp.yeey.whisperer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ensharp.yeey.whisperer.Common.VO.CloserStationVO;
import ensharp.yeey.whisperer.Common.ParseManager;
import ensharp.yeey.whisperer.Common.VO.BusStopVO;
import ensharp.yeey.whisperer.Common.VO.BusVO;
import ensharp.yeey.whisperer.Common.VO.DefaultInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExchangeInfoVO;
import ensharp.yeey.whisperer.Common.VO.ExitInfoVO;
import ensharp.yeey.whisperer.Common.VO.PathVO;
import ensharp.yeey.whisperer.Common.VO.StationVO;
import ensharp.yeey.whisperer.Common.VO.SubwayStationInfoVO;
import ensharp.yeey.whisperer.Common.VO.SubwayTimeTableVO;
import ensharp.yeey.whisperer.Common.VO.TimeVO;
import ensharp.yeey.whisperer.Common.VO.UseInfoVO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.support.v4.content.ContextCompat.startActivity;

class ODsayServiceManager {
    private static final ODsayServiceManager ourInstance = new ODsayServiceManager();

    static ODsayServiceManager getInstance() {
        return ourInstance;
    }

    private ODsayService odsayService;
    private JSONObject jsonObject;
    private ParseManager parseManager;
    ExcelManager excelManager;


    private PathVO path;
    private CloserStationVO closerStation;

    private Context context;
    private SubwayStationInfoVO station;
    private SubwayTimeTableVO timeTable;

    String departure;
    String destination;
    String stationName;

    String wayCode;

    Calendar cal;
    String strWeek;

    private static String TAG = "API Callback";

    private ODsayServiceManager() {
        parseManager = ParseManager.getInstance();
    }

    public void setContext(Context _context) { this.context = _context; }

    /**
     * 지하철 운행정보를 가져오는 API를 호출합니다.
     * ODsayService 객체는 싱글톤으로 생성됩니다.
     */
    public void initAPI(Context _context) {
        this.context = _context;
        odsayService = ODsayService.init(context, context.getString(R.string.odsay_key));
        excelManager = new ExcelManager(context);
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        cal = Calendar.getInstance();
        strWeek = null;
    }

    /**
     * API가 호출된 후 실행되는 콜백 메소드입니다.
     * 호출 결과를 로그값으로 나타냅니다.
     */
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();
            String message = "";
//            Log.e("jsonObject",String.valueOf(jsonObject));

            switch (api.name()) {
                case "SUBWAY_PATH": // 지하철 경로 검색
                    path = parseManager.parsePath(jsonObject);
                    // path 이용 메소드 올 곳
                    message = departure + "역에서 " + destination + "역까지 총 " + path.getGlobalStationCount() + "정거장이며 " +
                            "시간은 " + path.getGlobalTravelTime() + "분 소요됩니다.";
                    if(path.getExChangeInfoSet() != null) {
                        message += "환승역은 ";
                        for(ExchangeInfoVO exchangeInfoVO : path.getExchangeInfoList()){
                            message += exchangeInfoVO.getExName() + "역 ";
                        }
                        message += "이 있습니다.";
                    }
                    Log.e("PATHMESSAGE",message);
                    break;
                case "POINT_SEARCH":
                    // 가장 가까운 지하철 역 찾아서 전화하기
                    closerStation = parseManager.parseCloserStation(jsonObject);
                    CallStation(closerStation.getCloserStationList().get(Constant.MOST_CLOSER_STATION));
                    break;
                case "SUBWAY_STATION_INFO": // 지하철역 세부 정보
                    station = parseManager.parseStation(jsonObject);
                    // station 이용 메소드 올 곳
                    ((TextView)((Activity)context).findViewById(R.id.result)).setText(station.toString());

                    Log.e(TAG, "Subway: " + station.toString());
                        break;
                case "SUBWAY_TIME_TABLE":
                    timeTable = parseManager.parseTimeTable(jsonObject, wayCode);
                    int nWeek = cal.get(Calendar.DAY_OF_WEEK);

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String current_time = format.format(System.currentTimeMillis());
                    int count = 5;
                    int hour = Integer.parseInt(current_time.substring(0,2));
                    int minute = Integer.parseInt(current_time.substring(2,2));
                    int restTime = 0;
                    if(nWeek == 1){
                        //일요일
                    }
                    else if(nWeek == 7){
                        //토요일
                    }
                    else{
                        //평일
                        if( hour >= 1 && hour <=4 ){
                            message += "지하철 운행 시간이 아닙니다.";
                        }
                        else{
                            for(TimeVO timeVO : timeTable.getOrdTimeList()){
                                if(count == hour){
                                    String minuteList[] = timeVO.getList().split(" ");
                                    for(String m : minuteList){
                                        m = m.substring(0,2);
                                        if(Integer.parseInt(m) > minute){
                                            Log.e("m",m);
                                            restTime = Integer.parseInt(m) - minute;
                                            break;
                                        }
                                    }
                                    break;
                                }
                                else{

                                    count++;
                                }
                            }
                            message += stationName + "역의 상행 열차는 " + String.valueOf(restTime) + "분 후에 들어옵니다.";
                        }
                    }
                    Log.e("message",message);
                    break;
                default:
                    Log.e(TAG, "api 이름: " + api.name());
                    break;
            }

        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.e(TAG, "onError: API : " + api.name() + "\n" + errorMessage);
        }
    };

    //가까운 지하철역 코드 조회
    public void findCloserStationCode(double latitude, double longitude){
        odsayService.requestPointSearch(String.valueOf(latitude), String.valueOf(longitude), "5000", "2", onResultCallbackListener);
    }

    /**
     * 출발역과 도착역의 코드를 파라미터로 전달하면 이동 경로를 계산합니다.
     * requestSubwayPath는 비동기로 진행됩니다.
     * 계산된 이동 경로는 path에 저장됩니다.
     * @param departure 출발역 이름
     * @param destination 도착역 이름
     */
    public void calculatePath(String departure, String destination) {
        this.departure = departure;
        this.destination = destination;
        String startCode = excelManager.Find_Data(departure,Constant.STATION_NAME,Constant.STATION_CODE);
        String endCode = excelManager.Find_Data(destination,Constant.STATION_NAME,Constant.STATION_CODE);
        odsayService.requestSubwayPath("1000", startCode, endCode, "2", onResultCallbackListener);
    }


    //해당 역 코드로 전화번호를 찾아서 전화 걸기
    private void CallStation(StationVO mCloserStation){
        String stationNumber = excelManager.Find_Data(String.valueOf(mCloserStation.getStationID())
                , Constant.STATION_CODE, Constant.STATION_NUMBER);
        Log.e("stationNumber",stationNumber);
        Uri call = Uri.parse("tel:" + stationNumber);

        Intent call_intent = new Intent(Intent.ACTION_CALL, call);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            startActivity(context, call_intent,null);
        }
    }

    public void getSubwayInfo(String station) {
        odsayService.requestSubwayStationInfo(station, onResultCallbackListener);
    }

    /**
     * 지하철역의 시간표를 가져오는 메소드입니다.
     * 정보는 timeTable에 저장됩니다.
     * @param stationName 지하철역 이름
     * @param wayCode 상행/하행 여부
     */
    public void getSubwayTimeTable(String stationName, String wayCode) {
        this.stationName = stationName;
        this.wayCode = wayCode;
        String station_code = excelManager.Find_Data(stationName, Constant.STATION_NAME, Constant.STATION_CODE);
        odsayService.requestSubwayTimeTable(station_code, wayCode, onResultCallbackListener);
    }
}
