package ensharp.yeey.whisperer.Common.VO;

/**
 * 버스의 정보를 담는 VO입니다.
 */
public class BusVO {
    public String BusNO;    // 버스노선 번호
    public String Type; // 버스노선 타입
    public String BBID;  // 버스노선 ID

    public String getBusNO() {
        return BusNO;
    }

    public void setBusNO(String busNO) {
        BusNO = busNO;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getBBID() {
        return BBID;
    }

    public void setBBID(String BBID) {
        this.BBID = BBID;
    }

    @Override
    public String toString() {
        return "BUS [BusNO:" + BusNO
                + "BusType" + Type
                + "BusBBID" + BBID
                + "]";
    }
}
