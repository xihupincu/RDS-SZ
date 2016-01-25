package ReadXml;

/**
 * Created by CaoYu on 2015/12/12.
 */
public class Ticket {
    private int myorder;
    private String unit;
    private String detail;

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setMyorder(int myorder) {
        this.myorder = myorder;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMyorder() {
        return myorder;
    }

    public String getDetail() {
        return detail;
    }

    public String getUnit() {
        return unit;
    }
}
