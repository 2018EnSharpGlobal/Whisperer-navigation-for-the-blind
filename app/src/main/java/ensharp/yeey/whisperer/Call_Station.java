package ensharp.yeey.whisperer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import static android.support.v4.content.ContextCompat.startActivity;

public class Call_Station {

    Context context;

    //사용자 위치 변수 선언
    private double user_latitude;
    private double user_longitude;

    //역 정보 클래스 생성
    private Station_Info station_info;

    //Call_Station 생성자 (Context, 사용자 위도,경도 위치 받기,역 정보 클래스 생성)
    public Call_Station(Context _context, double _user_latitude, double _user_longitude) {

        this.context = _context;

        this.user_latitude = _user_latitude;
        this.user_longitude = _user_longitude;

        this.station_info = new Station_Info();
    }

    //가장 가까운 역 전화번호 찾기
    public String find_closer_station() {

        Station_Info.Per_Station_Info closer_station = null;
        double minmum_value = 0;

        for (Station_Info.Per_Station_Info station : station_info.station_infos) {
            double calculate_value = Math.pow(station.station_latitude - user_latitude, 2)
                    + Math.pow(station.station_longitude - user_longitude, 2);
            if (calculate_value < minmum_value) {
                minmum_value = calculate_value;
                closer_station = station;
            }
        }

        return closer_station.station_call_number;
    }

    //전화 걸어주는 함수
    public void call_closer_station() {

        String station_number = find_closer_station();

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + station_number));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent);
        }

    }
}
