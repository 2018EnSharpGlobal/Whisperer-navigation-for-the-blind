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
        ODsayServiceManager oDsayServiceManager = ODsayServiceManager.getInstance();
        oDsayServiceManager.find_closer_station_code(user_longitude,user_latitude);
    }

    //역 전화번호 찾는 함수
    private String Find_Station_number() {

        ExcelManager excelManager = new ExcelManager(context);

        String station_number = excelManager.Find_Data(station_Id, Constant.STATION_CODE, Constant.STATION_NUMBER);

        return station_number;
    }

    //역 전화번호에 전화거는 함수
    public void Call_Station() {

    }
}
