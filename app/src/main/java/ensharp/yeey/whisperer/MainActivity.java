package ensharp.yeey.whisperer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONObject;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ODsayService odsayService;

    private final int CODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSubwayAPI();

//        Call_Station_Number call_station_number = new Call_Station_Number(this);
//        call_station_number.Call_Station();

        //권한
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(this, neededPermissions, CODE_PERMISSIONS );

    }

    //허가 요청후 오버라이드 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    /**
     * 지하철 운행정보를 가져오는 API를 호출합니다.
     * ODsayService 객체는 싱글톤으로 생성됩니다.
     */
    private void initSubwayAPI() {
        odsayService = ODsayService.init(MainActivity.this, getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
    }
}
