package ensharp.yeey.whisperer.Common.VO;

import java.io.Serializable;

/**
 * 가까운 지하철역 정보를 담는 VO입니다.
 */

public class CloserStationVO implements  Serializable{
    private String stationName;     // 대중교통 POI 이름
    private int stationId;      //대중교통 POI ID
    private double gps_x;   //경위도
    private double gps_y;   //경위도

    public String getStationName() { return stationName; }

    public void setStationName(String stationName) { this.stationName = stationName; }

    public int getStationId() { return stationId; }

    public void setStationId(int stationId) { this.stationId = stationId; }

    public double getGps_x() { return gps_x; }

    public void setGps_x(double gps_x) { this.gps_x = gps_x; }

    public double getGps_y() { return gps_y; }

    public void setGps_y(double gps_y) { this.gps_y = gps_y; }

    @Override
    public String toString() {
        return "Closer_Station [stationName=" + stationName
                + ", stationId=" + String.valueOf(stationId)
                + ", gps_x=" + String.valueOf(gps_x)
                + ", gps_y=" + String.valueOf(gps_y)
                + "]\n";
    }

}


