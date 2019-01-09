package ensharp.yeey.whisperer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        NetworkTask networkTask = new NetworkTask(this, Constant.Closer_Station_API+"197529.91541/450688.46452",Constant.FIND_CLOSER_STATION);
//        NetworkTask networkTask = new NetworkTask(this,Constant.Find_Number_API+"0335/",Constant.FIND_STATION_NUMBER);
//        networkTask.execute();
//
//        //싱글톤 생성, Key 값을 화용하여 객체 생성
//        final ODsayService odsayService = ODsayService.init(this, Constant.ODSAY_API_KEY);
//
//        //서버 연결 제한 시간 (단위(초), default : 5초)
//        odsayService.setReadTimeout(5000);
//        //데이터 획득 제한 시간(단위(초), default : 5초)
//        odsayService.setConnectionTimeout(5000);
//
//        //콜백 함수 구현
//        final OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
//            @Override
//            public void onSuccess(ODsayData oDsayData, API api) {
//                try {
//                    Log.e("success","");
//                    //API Value 는 API 호출 메소드 명을 따라감
//
//                    if (api == API.POINT_SEARCH) {
//                        JSONObject result = oDsayData.getJson().getJSONObject("result");
//                        JSONArray station_array = result.getJSONArray("station");
//                        JSONObject station = station_array.getJSONObject(5);
//                        String station_ID = station.getString("stationID");
//                        Log.e("stationID",station_ID);
//
//                        //odsayService.requestSubwayStationInfo(station_ID,this);
//
//                    }
//                    else if(api == API.SUBWAY_STATION_INFO){
//                        Log.e("data",oDsayData.getJson().toString());
//                        JSONObject default_Info = oDsayData.getJson().getJSONObject("result").getJSONObject("defaultInfo");
//                        String station_tel = default_Info.getString("tel");
//                        Log.e("station_tel",station_tel);
//                        //String station_tel = station.getString("tel");
//                        //Log.e("tel",station_tel);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//                @Override
//            public void onError(int i, String s, API api) {
//                Log.e("error",s);
//                if(api == API.POINT_SEARCH) {}
//            }
//        };
//
//        odsayService.requestPointSearch("126.933361407195","37.3643392278118","5000","2",onResultCallbackListener);

        Call_Station_Number call_station_number = new Call_Station_Number(this);
        call_station_number.Call_Station();
    }
}
