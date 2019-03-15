package ensharp.yeey.whisperer.Common.VO;

/**
 * 지하철역의 이용정보를 담고 있는 VO입니다.
 */
public class UseInfoVO {
    public int platform; // 플랫폼
    public int meetingPlace; // 만남의 장소
    public int restroom;    // 화장실
    public int crossOver;   // 반대편 횡단
    public int publicPlace; // 현장 사무소
    public int handicapCount;   // 장애인 편의시설
    public int parkingCount;    // 환승 주차장
    public int bicycleCount;    // 자전거 보관소
    public int civilCount;  // 민원 안내

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(int meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public int getRestroom() {
        return restroom;
    }

    public void setRestroom(int restroom) {
        this.restroom = restroom;
    }

    public int getCrossOver() {
        return crossOver;
    }

    public void setCrossOver(int crossOver) {
        this.crossOver = crossOver;
    }

    public int getPublicPlace() {
        return publicPlace;
    }

    public void setPublicPlace(int publicPlace) {
        this.publicPlace = publicPlace;
    }

    public int getHandicapCount() {
        return handicapCount;
    }

    public void setHandicapCount(int handicapCount) {
        this.handicapCount = handicapCount;
    }

    public int getParkingCount() {
        return parkingCount;
    }

    public void setParkingCount(int parkingCount) {
        this.parkingCount = parkingCount;
    }

    public int getBicycleCount() {
        return bicycleCount;
    }

    public void setBicycleCount(int bicycleCount) {
        this.bicycleCount = bicycleCount;
    }

    public int getCivilCount() {
        return civilCount;
    }

    public void setCivilCount(int civilCount) {
        this.civilCount = civilCount;
    }

    @Override
    public String toString() {
        return "UseInfoVO [platform:" + platform
                + ", meetingPlace:" + meetingPlace
                + ", restroom:" + restroom
                + ", crossOver:" + crossOver
                + ", publicPlace:" + publicPlace
                + ", handicapCount:" + handicapCount
                + ", parkingCount:" + parkingCount
                + ", bicycleCount:" + bicycleCount
                + ", civilCount:" + civilCount
                + "]";
    }
}
