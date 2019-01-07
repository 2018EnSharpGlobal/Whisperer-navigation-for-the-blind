package ensharp.yeey.whisperer;

import android.content.PeriodicSync;

import java.util.ArrayList;
import java.util.List;

//역에 대한 정보들을 가지고 있는 Class
public class Station_Info {

    //역에대한 정보들을 리스트로 선언
    public List<Per_Station_Info> station_infos;

    //Station_Info 생성자 (역에 대한 정보 초기화)
    public Station_Info(){
        station_infos = new ArrayList<>();

        //어린이 대공원역 정보추가
        Per_Station_Info Sejong_University = new Per_Station_Info
                (Constant.Sejong_University_latitude,Constant.Sejong_University_longitude,Constant.Sejong_University_number);
        station_infos.add(Sejong_University);

    }

    //역에 대한 정보들을 가지는 내부 Class
    public class Per_Station_Info{

        public double station_latitude;
        public double station_longitude;
        public String station_call_number;

        private Per_Station_Info(double _station_latitude, double _station_longitude, String _station_call_number){
            this.station_latitude = _station_latitude;
            this.station_longitude = _station_longitude;
            this.station_call_number = _station_call_number;
        }

    }
}
