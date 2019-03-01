package ensharp.yeey.whisperer.Common.VO;

import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.List;

/**
 * 가까운 지하철역 정보를 담는 VO입니다.
 */

public class CloserStationVO{


    private JsonElement station ;
    private List<StationVO> closerStationList;

    public JsonElement getStation() { return station; }

    public void setStation(JsonElement station) { this.station = station; }

    public List<StationVO> getCloserStationList() { return closerStationList; }

    public void setCloserStationList(List<StationVO> closerStationList) { this.closerStationList = closerStationList ; }

}


