package ReadXml;

import java.sql.Timestamp;

/**
 * Created by CaoYu on 2015/12/11.
 */
public class Task {
    private String ID;
    private String  NPR;
    private Timestamp NPTTIME;
    private String CZMD;
    private String ALLCZDW;
    private String LX;
    private String CZPBH;
    private  String SFZCCZ;

    public void setALLCZDW(String ALLCZDW) {
        this.ALLCZDW = ALLCZDW;
    }

    public void setCZMD(String CZMD) {
        this.CZMD = CZMD;
    }

    public void setCZPBH(String CZPBH) {
        this.CZPBH = CZPBH;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setLX(String LX) {
        this.LX = LX;
    }

    public void setNPR(String NPR) {
        this.NPR = NPR;
    }

    public void setNPTTIME(Timestamp NPTTIME) {
        this.NPTTIME = NPTTIME;
    }

    public void setSFZCCZ(String SFZCCZ) {
        this.SFZCCZ = SFZCCZ;
    }

    public String getALLCZDW() {
        return ALLCZDW;
    }

    public String getCZMD() {
        return CZMD;
    }

    public String getCZPBH() {
        return CZPBH;
    }

    public String getID() {
        return ID;
    }

    public String getLX() {
        return LX;
    }

    public String getNPR() {
        return NPR;
    }

    public String getSFZCCZ() {
        return SFZCCZ;
    }

    public Timestamp getNPTTIME() {
        return NPTTIME;
    }
}
