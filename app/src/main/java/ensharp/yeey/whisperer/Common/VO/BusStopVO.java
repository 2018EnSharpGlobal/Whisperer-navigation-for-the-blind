package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * 지하철역 근처의 버스 정류장 정보와 버스 정보를 담고 있는 VO입니다.
 */
public class BusStopVO {
    public String StopName; // 버스정류장 명칭
    public String StopID;   // 버스정류장 ID
    public JsonElement Bus;
    public List<BusVO> busList; // 버스노선정보 리스트

    public String getStopName() {
        return StopName;
    }

    public void setStopName(String stopName) {
        StopName = stopName;
    }

    public String getStopID() {
        return StopID;
    }

    public void setStopID(String stopID) {
        StopID = stopID;
    }

    public JsonElement getBus() {
        return Bus;
    }

    public void setBus(JsonElement bus) {
        Bus = bus;
    }

    public List<BusVO> getBusList() {
        return busList;
    }

    public void setBusList(List<BusVO> busList) {
        this.busList = busList;
    }

    @Override
    public String toString() {
        return "BusStation [stopName:" + StopName
                + ", stopID:" + StopID
                + ", is busList null?" + String.valueOf(Bus == null)
                + "]";
    }
}
