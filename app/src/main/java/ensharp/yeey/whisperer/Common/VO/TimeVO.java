package ensharp.yeey.whisperer.Common.VO;

/**
 * 지하철 시간표에 들어갈 기본적인 내용을 담는 VO입니다.
 */
public class TimeVO {
    int ldx;	// 시간
    String list;	// 시간 data
    String expList;	// 급행시간 data

    public int getLdx() {
        return ldx;
    }

    public void setLdx(int ldx) {
        this.ldx = ldx;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getExpList() {
        return expList;
    }

    public void setExpList(String expList) {
        this.expList = expList;
    }

    @Override
    public String toString() {
        return "Time [ldx=" + ldx
                + ", list=" + list
                + ", expList=" + expList
                + "]";
    }
}
