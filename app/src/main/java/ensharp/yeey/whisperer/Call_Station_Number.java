package ensharp.yeey.whisperer;

import android.content.Context;
import android.util.Log;

//odsay import
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

//json import
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class Call_Station_Number {

    private Context context;

    private String station_Id;

    private double user_longitude;
    private double user_latitude;

    //test 생성자
    public Call_Station_Number(Context _context) {
        this.context = _context;
    }

    //Real 생성자
    public Call_Station_Number(Context _context, double _user_longitude, double _user_latitude) {
        this.context = _context;
        this.user_longitude = _user_longitude;
        this.user_latitude = _user_latitude;
    }

    //가까운 역 찾는 함수
    private void Find_Closer_Station() {

        //싱글톤 생성, Key 값을 화용하여 객체 생성
        final ODsayService odsayService = ODsayService.init(context, Constant.ODSAY_API_KEY);

        //서버 연결 제한 시간 (단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        //데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);

        //콜백 함수 구현
        final OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData oDsayData, API api) {
                try {
                    //가까운 위치의 역 Station ID 찾기
                    if (api == API.POINT_SEARCH) {
                        JSONObject result = oDsayData.getJson().getJSONObject("result");
                        JSONArray station_array = result.getJSONArray("station");
                        JSONObject station = station_array.getJSONObject(5);
                        station_Id = station.getString("stationID");
                        Log.e("stationID", station_Id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                Log.e("error", s);
                if (api == API.POINT_SEARCH) {
                }
            }
        };

        //test 함수
        odsayService.requestPointSearch("126.933361407195", "37.3643392278118", "5000", "2", onResultCallbackListener);
//        odsayService.requestPointSearch(String.valueOf(user_longitude),String.valueOf(user_latitude),"5000","2",onResultCallbackListener);
    }

    //역 전화번호 찾는 함수
    private String Find_Station_number() {
        String station_number = null;

        return station_number;
    }

    //역 전화번호에 전화거는 함수
    public void Call_Station() {
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            InputStream inputStream = context.getResources().getAssets().open("station_data.xls");
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);
            int MaxColumn = 4, RowStart = 1, RowEnd = sheet.getColumn(MaxColumn - 1).length -1, ColumnStart = 2, ColumnEnd = sheet.getRow(2).length - 1;
            Log.e("MaxColumn",String.valueOf(MaxColumn));
            Log.e("RowStart",String.valueOf(RowStart));
            Log.e("RowEnd",String.valueOf(RowEnd));
            Log.e("ColumnStart",String.valueOf(ColumnStart));
            Log.e("ColumnEnd",String.valueOf(ColumnEnd));
            for(int row = RowStart;row <= RowEnd;row++) {
                String station_code = sheet.getCell(ColumnStart, row).getContents();
                if(station_code.equals("919")){
                    Log.e("number",sheet.getCell(3,row).getContents());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }
}
