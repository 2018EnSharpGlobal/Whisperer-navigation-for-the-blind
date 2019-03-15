package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * 지하철역의 출구정보를 가지고 있는 VO입니다.
 */
public class ExitInfoVO {
    public String gateNo;   // 출구 번호
    public JsonElement gateLink;
    public List<String> gateLinkList;   // 주요 명칭
    public JsonElement BUSSTOP;
    public List<BusStopVO> busStopList; // 출구별 버스정류장 정보

    public String getGateNo() {
        return gateNo;
    }

    public void setGateNo(String gateNo) {
        this.gateNo = gateNo;
    }

    public JsonElement getGateLink() {
        return gateLink;
    }

    public void setGateLink(JsonElement gateLink) {
        this.gateLink = gateLink;
    }

    public List<String> getGateLinkList() {
        return gateLinkList;
    }

    public void setGateLinkList(List<String> gateLinkList) {
        this.gateLinkList = gateLinkList;
    }

    public JsonElement getBUSSTOP() {
        return BUSSTOP;
    }

    public void setBUSSTOP(JsonElement BUSSTOP) {
        this.BUSSTOP = BUSSTOP;
    }

    public List<BusStopVO> getBusStopList() {
        return busStopList;
    }

    public void setBusStopList(List<BusStopVO> busStopList) {
        this.busStopList = busStopList;
    }

    @Override
    public String toString() {
        return "Gate [gateNo:" + gateNo
                + ", gateLinkNull?" + String.valueOf(gateLink == null)
                + ", isBusStopListNull?" + String.valueOf(busStopList == null)
                + "]";
    }
}
