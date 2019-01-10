package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.util.List;

public class PathVO {
    private String globalStartName;
    private String globalEndName;
    private String globalTravelTime;
    private String globalStationCount;
    private JsonElement exChangeInfoSet;
    private List<ExchangeInfoVO> exchangeInfoList;

    public String getGlobalStartName() {
        return globalStartName;
    }

    public void setGlobalStartName(String globalStartName) {
        this.globalStartName = globalStartName;
    }

    public String getGlobalEndName() {
        return globalEndName;
    }

    public void setGlobalEndName(String globalEndName) {
        this.globalEndName = globalEndName;
    }

    public String getGlobalTravelTime() {
        return globalTravelTime;
    }

    public void setGlobalTravelTime(String globalTravelTime) {
        this.globalTravelTime = globalTravelTime;
    }

    public String getGlobalStationCount() {
        return globalStationCount;
    }

    public void setGlobalStationCount(String globalStationCount) {
        this.globalStationCount = globalStationCount;
    }

    public JsonElement getExChangeInfoSet() {
        return exChangeInfoSet;
    }

    public void setExChangeInfoSet(JsonElement exChangeInfoSet) {
        this.exChangeInfoSet = exChangeInfoSet;
    }

    public List<ExchangeInfoVO> getExchangeInfoList() {
        return exchangeInfoList;
    }

    public void setExchangeInfoList(List<ExchangeInfoVO> exchangeInfoList) {
        this.exchangeInfoList = exchangeInfoList;
    }

    @Override
    public String toString() {
        return "Path [start=" + globalStartName
                + ", end=" + globalEndName
                + ", time=" + globalTravelTime
                + ", count=" + globalStationCount
                + ", isExchangeNull?" + String.valueOf(exChangeInfoSet == null)
                + "]";
    }
}
