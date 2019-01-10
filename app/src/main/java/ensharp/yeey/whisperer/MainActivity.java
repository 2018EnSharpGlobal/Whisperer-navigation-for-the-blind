package ensharp.yeey.whisperer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONObject;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ODsayService odsayService;

    private final int CODE_PERMISSIONS = 1;

    //indooratlas
    IALocationManager mIALocationManager;
    IALocationListener mIALocationListener;
    IARegion.Listener mRegionListener;

    String TAG = "INDOOR_ATLAS";

    MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        initSubwayAPI();

//        Call_Station_Number call_station_number = new Call_Station_Number(this);
//        call_station_number.Call_Station();

        //권한
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        };

//        ActivityCompat.requestPermissions(this, neededPermissions, 1);

        //API 23이상이면 checkVerify() 실행, 아니면 토스트 띄우기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            checkVerify();
        }else{
            Toast.makeText(this,"API 26이상 부터 사용할 수 있는 App 입니다.",Toast.LENGTH_LONG).show();
            this.finish();
        }


        /*
        //indooratlas
        mIALocationManager = IALocationManager.create(this);

        mIALocationListener = new IALocationListener() {

            // Called when the location has changed.
            @Override
            public void onLocationChanged(IALocation location) {

                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLongitude());
                Log.d(TAG, "Floor number: " + location.getFloorLevel());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
        };

        //층 변화, 실내, 실외 변화 감지하는 함수
        mRegionListener = new IARegion.Listener(){
            IARegion mCurrentFloorPlan = null;

            @Override
            public void onEnterRegion(IARegion region) {
                if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                    Log.d(TAG, "Entered " + region.getName());
                    Log.d(TAG, "floor plan ID: " + region.getId());
                    mCurrentFloorPlan = region;
                }
                // 실외 탐지
                else if (region.getType() == IARegion.TYPE_VENUE){
                    Log.d(TAG, "Location changed to " + region.getId());
                }
            }

            @Override
            public void onExitRegion(IARegion region) {
                //이전에 들어왔던 지역 나갈 때
                if (region.getType() == IARegion.TYPE_FLOOR_PLAN){
                    mCurrentFloorPlan = null;
                }
            }
        };

        //감지하는 콜백함수 등록
        mIALocationManager.registerRegionListener(mRegionListener);
        */
    }

    /*
    //위치 계속 업데이트
    @Override
    protected void onResume(){
        super.onResume();
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
    }

    //업데이트된 정보 받기 중지
    @Override
    protected void onPause() {
        super.onPause();
        mIALocationManager.removeLocationUpdates(mIALocationListener);
    }

    //할당된 리소스 해제
    @Override
    protected void onDestroy() {
        mIALocationManager.destroy();
        super.onDestroy();
    }
*/
    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify(){

        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        };

        if( checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            //설명 보여주기
            if(shouldShowRequestPermissionRationale(Manifest.permission.CHANGE_WIFI_STATE)){
                // ...
            }
            requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE},1);
        }
        else{
            Toast.makeText(this,"호호로롤",Toast.LENGTH_LONG).show();
        }
    }

    //허가 요청 후 오버라이드 함수
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if(requestCode == 1){
//
//            if(grantResults.length > 0){
//
//                for(int i=0 ; i < grantResults.length ; i++){
//
//                    //권한 요청 한개라도 거부한다면
//                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
//                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해야 앱을 이용할 수 있습니다").setPositiveButton("종료", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int i) {
//                                dialog.dismiss();
//                                mainActivity.finish();
//                            }
//                        }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
//                                getApplicationContext().startActivity(intent);
//                            }
//                        }).setCancelable(false).show();
//
//                        return;
//                    }
//                }
//            }
//        }
//    }


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
