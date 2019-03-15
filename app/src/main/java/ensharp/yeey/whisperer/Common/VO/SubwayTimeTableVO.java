package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.util.List;

public class SubwayTimeTableVO {
    public String stationName;  // 지하철역 이름
    public int stationID;   // 지하철역 ID
    public String laneName; // 노선명
    public String upWay;    // 상행방향
    public String downWay;  // 하행방향
    public JsonElement OrdList;
    public JsonElement SatList;
    public JsonElement SunList;
    public TimeVO ordTime;
    public TimeVO satTime;
    public TimeVO sunTime;
    public List<TimeVO> ordTimeList;   // 평일 시간표 정보
    public List<TimeVO> satTimeList;    // 토요일 시간표 정보
    public List<TimeVO> sunTimeList;    // 일요일 시간표 정보

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public String getUpWay() {
        return upWay;
    }

    public void setUpWay(String upWay) {
        this.upWay = upWay;
    }

    public String getDownWay() {
        return downWay;
    }

    public void setDownWay(String downWay) {
        this.downWay = downWay;
    }

    public JsonElement getOrdList() {
        return OrdList;
    }

    public TimeVO getOrdTime() {
        return ordTime;
    }

    public void setOrdTime(TimeVO ordTime) {
        this.ordTime = ordTime;
    }

    public TimeVO getSatTime() {
        return satTime;
    }

    public void setSatTime(TimeVO satTime) {
        this.satTime = satTime;
    }

    public TimeVO getSunTime() {
        return sunTime;
    }

    public void setSunTime(TimeVO sunTime) {
        this.sunTime = sunTime;
    }

    public void setOrdList(JsonElement ordList) {
        OrdList = ordList;
    }

    public JsonElement getSatList() {
        return SatList;
    }

    public void setSatList(JsonElement satList) {
        SatList = satList;
    }

    public JsonElement getSunList() {
        return SunList;
    }

    public void setSunList(JsonElement sunList) {
        SunList = sunList;
    }

    public List<TimeVO> getOrdTimeList() {
        return ordTimeList;
    }

    public void setOrdTimeList(List<TimeVO> ordTimeList) {
        this.ordTimeList = ordTimeList;
    }

    public List<TimeVO> getSatTimeList() {
        return satTimeList;
    }

    public void setSatTimeList(List<TimeVO> satTimeList) {
        this.satTimeList = satTimeList;
    }

    public List<TimeVO> getSunTimeList() {
        return sunTimeList;
    }

    public void setSunTimeList(List<TimeVO> sunTimeList) {
        this.sunTimeList = sunTimeList;
    }

    @Override
    public String toString() {
        return "SubwayTimeTable [stationName=" + stationName
                + ", stationID=" + stationID
                + ", laneName=" + laneName
                + ", upWay=" + upWay
                + ", downWay=" + downWay
                + ", is OrdList null?" + String.valueOf(OrdList == null)
                + ", is SatList null?" + String.valueOf(SatList == null)
                + ", is SunList null?" + String.valueOf(SunList == null)
                + ", is ordTimeList null?" + String.valueOf(ordTimeList == null)
                + ", is satTimeList null?" + String.valueOf(satTimeList == null)
                + ", is sunTimeList null?" + String.valueOf(sunTimeList == null)
                + "]";
    }
}
