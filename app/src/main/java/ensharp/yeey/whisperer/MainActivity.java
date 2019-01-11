package ensharp.yeey.whisperer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IARegion;

public class MainActivity extends AppCompatActivity {

    private ODsayServiceManager oDsayServiceManager;

    private final int CODE_PERMISSIONS = 1;

    //indooratlas
    IALocationManager mIALocationManager;
    IALocationListener mIALocationListener;
    IARegion.Listener mRegionListener;

    String TAG = "INDOOR_ATLAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSubwayAPI();

        int MyVersion = Build.VERSION.SDK_INT ;
        if(MyVersion >= Build.VERSION_CODES.O){
            if(!checkIfAlreadyhavePermission()){
                requestForSpecificPermission();
            }
        } else{
            Toast.makeText(this,"버전이 맞지 않아 이 앱을 사용할 수 없습니다.",Toast.LENGTH_LONG).show();
            this.finish();
        }

//        //권한
//        String[] neededPermissions = {
//                Manifest.permission.CHANGE_WIFI_STATE,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.CALL_PHONE
//        };

//        ActivityCompat.requestPermissions(this, neededPermissions, 1);

//        //API 23이상이면 checkVerify() 실행, 아니면 토스트 띄우기
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            checkVerify();
//        }else{
//            Toast.makeText(this,"API 26이상 부터 사용할 수 있는 App 입니다.",Toast.LENGTH_LONG).show();
//            this.finish();
//        }


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
//    @TargetApi(Build.VERSION_CODES.M)
//    public void checkVerify(){
//
//        String[] neededPermissions = {
//                Manifest.permission.CHANGE_WIFI_STATE,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.CALL_PHONE
//        };
//
//        if( checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
//            //설명 보여주기
//            if(shouldShowRequestPermissionRationale(Manifest.permission.CHANGE_WIFI_STATE)){
//                // ...
//            }
//            requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE,
//                    Manifest.permission.ACCESS_WIFI_STATE,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.CALL_PHONE},1);
//        }
//        else{
//            Toast.makeText(this,"호호로롤",Toast.LENGTH_LONG).show();
//        }
//    }

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
     * 지하철 운행정보를 가져오는 API를 호출하고 초기화합니다.
     */
    private void initSubwayAPI() {
        oDsayServiceManager = ODsayServiceManager.getInstance();
        oDsayServiceManager.setMainActivity(this);
        oDsayServiceManager.initAPI();
    }

    private boolean checkIfAlreadyhavePermission(){
        int call_phone_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int change_wifi_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        int access_wifi_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int access_location_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.e("call_phone_permission", String.valueOf(call_phone_permission));
        Log.e("change_wifi_permission", String.valueOf(change_wifi_permission));
        Log.e("access_wifi_permission", String.valueOf(access_wifi_permission));
        Log.e("access_location_permission", String.valueOf(access_location_permission));

        if (call_phone_permission == PackageManager.PERMISSION_GRANTED &&
                change_wifi_permission == PackageManager.PERMISSION_GRANTED &&
                access_wifi_permission == PackageManager.PERMISSION_GRANTED &&
                access_location_permission == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            return false;
        }
    }

    private void requestForSpecificPermission(){
        //권한
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        };

        Log.e("1","1");

        ActivityCompat.requestPermissions(this, neededPermissions, 101);

        Log.e("2","2");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        Log.e("requestCode",String.valueOf(requestCode));
        for(int i=0 ; i< permissions.length; i++){
            Log.e("permissions",permissions[i].toString());
        }
        for(int i= 0 ; i< grantResults.length; i++){
            Log.e("grantResults", String.valueOf(grantResults[i]));
        }

        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
