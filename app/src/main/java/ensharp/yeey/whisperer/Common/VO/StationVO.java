package ensharp.yeey.whisperer.Common.VO;

import java.io.Serializable;

public class StationVO  implements Serializable {

    private int stationClass;
    private String stationName;
    private int stationID;
    private int type;
    private String laneName;
    private String laneCity;
    private int stationType;
    private double x;
    private double y;
    private int nonstopStation;

    public void setStationClass (int stationClass) { this.stationClass = stationClass ; }
    public void setStationName (String stationName) { this.stationName = stationName ; }
    public void setStationID (int stationID) { this.stationID = stationID ; }
    public void setType (int type) { this.type = type ; }
    public void setLaneName (String laneName) { this.laneName = laneName ; }
    public void setLaneCity (String laneCity) { this.laneCity = laneCity ; }
    public void setStationType (int stationType) { this.stationType = stationType ; }
    public void setGps_X (double x) { this.x = x ; }
    public void setGps_Y (double y) { this.y = y ; }
    public void setNonstopStation (int nonstopStation) { this.nonstopStation = nonstopStation ; }

    public int getStationClass () { return stationClass ; }
    public String getStationName() { return stationName ; }
    public int getStationID() { return  stationID ; }
    public int getType() { return type ; }
    public String getLaneName() { return laneName ; }
    public String getLaneCity() { return laneCity ; }
    public int getStationType() { return stationType ; }
    public double getX() { return x ; }
    public double getY() { return y ; }
    public int getNonstopStation() { return nonstopStation ; }

    @Override
    public String toString() {
        return "Station [stationClass =" + stationClass
                + ", stationName =" + stationName
                + ", stationID =" + stationID
                + ", type =" + type
                + ", laneName =" + laneName
                + ", laneCity =" + laneCity
                + ", stationType =" + stationType
                + ", gps_X =" + x
                + ", gps_Y =" + y
                + ", nonstopStation =" + nonstopStation
                + "]\n";
    }


}
