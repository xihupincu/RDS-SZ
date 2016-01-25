package DataManager;

import java.util.Date;

/**
 * Created by CaoYu on 2015/12/4.
 */
public class Task {
    private int success;
    private  String CZMD;
    private Date CZSJ;

    public void setCZMD(String CZMD) {
        this.CZMD = CZMD;
    }

    public void setCZSJ(Date CZSJ) {
        this.CZSJ = CZSJ;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public Date getCZSJ() {
        return CZSJ;
    }

    public int getSuccess() {
        return success;
    }

    public String getCZMD() {
        return CZMD;
    }
}
