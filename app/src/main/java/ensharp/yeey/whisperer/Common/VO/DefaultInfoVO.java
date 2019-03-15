package ensharp.yeey.whisperer.Common.VO;

/**
 * 지하철역의 기본 역 정보를 담고 있는 VO입니다.
 */
public class DefaultInfoVO {
    public String address;  // 역 주소
    public String newAddress;   // 도로명 주소
    public String tel;  // 역 전화번호

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "Default [address=" + address
                + ", newAddress=" + newAddress
                + ", tel=" + tel
                + "]";
    }
}
