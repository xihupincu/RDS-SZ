package DataManager;

/**
 * Created by CaoYu on 2015/12/4.
 */
public class TaskTable {
    private int order;
    private String unit;
    private String detail;

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getOrder() {
        return order;
    }

    public String getDetail() {
        return detail;
    }

    public String getUnit() {
        return unit;
    }
}
