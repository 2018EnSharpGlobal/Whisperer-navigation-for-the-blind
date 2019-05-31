package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * 지하철역 세부정보를 저장하는 VO입니다.
 */
public class SubwayStationInfoVO {
    private String stationName; // 지하철역 이름
    private int stationID;  // 지하철역 ID
    private int type;   // 노선 종류
    private String laneName;    // 노선명
    private JsonElement exOBJ;
    private List<SubwayStationInfoVO> exOBJList;    // 환승역 리스트
    private JsonElement defaultInfo;
    private DefaultInfoVO stationDefaultInfo;   // 기본 역 정보
    private JsonElement useInfo;
    private UseInfoVO stationUseInfo;   // 이용 정보
    private JsonElement exitInfo;
    private List<ExitInfoVO> stationExitInfoList; // 출구 정보

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public JsonElement getExOBJ() {
        return exOBJ;
    }

    public void setExOBJ(JsonElement exOBJ) {
        this.exOBJ = exOBJ;
    }

    public List<SubwayStationInfoVO> getExOBJList() {
        return exOBJList;
    }

    public void setExOBJList(List<SubwayStationInfoVO> exOBJList) {
        this.exOBJList = exOBJList;
    }

    public JsonElement getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(JsonElement defaultInfo) {
        this.defaultInfo = defaultInfo;
    }

    public DefaultInfoVO getStationDefaultInfo() {
        return stationDefaultInfo;
    }

    public void setStationDefaultInfo(DefaultInfoVO stationDefaultInfo) {
        this.stationDefaultInfo = stationDefaultInfo;
    }

    public JsonElement getUseInfo() {
        return useInfo;
    }

    public void setUseInfo(JsonElement useInfo) {
        this.useInfo = useInfo;
    }

    public UseInfoVO getStationUseInfo() {
        return stationUseInfo;
    }

    public void setStationUseInfo(UseInfoVO stationUseInfo) {
        this.stationUseInfo = stationUseInfo;
    }

    public JsonElement getExitInfo() {
        return exitInfo;
    }

    public void setExitInfo(JsonElement exitInfo) {
        this.exitInfo = exitInfo;
    }

    public List<ExitInfoVO> getStationExitInfoList() {
        return stationExitInfoList;
    }

    public void setStationExitInfoList(List<ExitInfoVO> stationExitInfoList) {
        this.stationExitInfoList = stationExitInfoList;
    }

    @Override
    public String toString() {
        return "Station [name=" + stationName
                + ", stationID=" + stationID
                + ", type=" + type
                + ", laneName=" + laneName
                + ", isExOBJ null?" + String.valueOf(exOBJ == null)
                + ", isUseInfo null?" + String.valueOf(useInfo == null)
                + ", isExitInfo null?" + String.valueOf(exitInfo == null)
                + "]";
    }
}
