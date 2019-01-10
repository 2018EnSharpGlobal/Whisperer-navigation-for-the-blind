package ensharp.yeey.whisperer.Common.VO;

import java.io.Serializable;

/**
 * 환승정보를 담는 VO입니다.
 * 출발역과 환승역, 빠른 출구, 예상 이동 시간에 대한 정보를 담고 있습니다.
 */
public class ExchangeInfoVO implements Serializable {
    private String laneName;    // 승차노선 명
    private String startName;   // 승차역 명
    private String exName;  // 환승역 명
    private int exSID;  // 환승역 ID
    private int fastTrain;  // 빠른 환승 객차 번호
    private int fastDoor;   // 빠른 환승 객차 문 번호
    private int exWalkTime; // 환승소요시간 (초)

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getExName() {
        return exName;
    }

    public void setExName(String exName) {
        this.exName = exName;
    }

    public int getExSID() {
        return exSID;
    }

    public void setExSID(int exSID) {
        this.exSID = exSID;
    }

    public int getFastTrain() {
        return fastTrain;
    }

    public void setFastTrain(int fastTrain) {
        this.fastTrain = fastTrain;
    }

    public int getFastDoor() {
        return fastDoor;
    }

    public void setFastDoor(int fastDoor) {
        this.fastDoor = fastDoor;
    }

    public int getExWalkTime() {
        return exWalkTime;
    }

    public void setExWalkTime(int exWalkTime) {
        this.exWalkTime = exWalkTime;
    }

    @Override
    public String toString() {
        return "Path [start=" + startName
                + ", exchange=" + exName
                + ", code=" + exSID
                + ", fast train=" + fastTrain
                + ", fast door=" + fastDoor
                + ", walk time=" + exWalkTime
                + "]\n";
    }
}
