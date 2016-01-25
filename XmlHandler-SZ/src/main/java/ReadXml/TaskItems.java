package ReadXml;

import java.sql.Timestamp;

/**
 * Created by CaoYu on 2015/12/11.
 */
public class TaskItems {
    private String ID;
    private String LSDX;
    private int EOID;
    private String JSR;
    private Timestamp XLSJ;
    private String XLR;
    private int XH;
    private int DINDEX;
    private String SFWC;
    private String CZDW;
    private String CZNR;
    private String SFQR;
    private int OINDEX;
    private String ZXR;
    private Timestamp ZXSJ;

    public String getID() {
        return ID;
    }

    public void setCZDW(String CZDW) {
        this.CZDW = CZDW;
    }

    public void setCZNR(String CZNR) {
        this.CZNR = CZNR;
    }

    public void setDINDEX(int DINDEX) {
        this.DINDEX = DINDEX;
    }

    public void setEOID(int EOID) {
        this.EOID = EOID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setJSR(String JSR) {
        this.JSR = JSR;
    }

    public void setLSDX(String LSDX) {
        this.LSDX = LSDX;
    }

    public void setOINDEX(int OINDEX) {
        this.OINDEX = OINDEX;
    }

    public void setSFQR(String SFQR) {
        this.SFQR = SFQR;
    }

    public void setSFWC(String SFWC) {
        this.SFWC = SFWC;
    }

    public void setXH(int XH) {
        this.XH = XH;
    }

    public void setXLR(String XLR) {
        this.XLR = XLR;
    }

    public void setXLSJ(Timestamp XLSJ) {
        this.XLSJ = XLSJ;
    }

    public void setZXR(String ZXR) {
        this.ZXR = ZXR;
    }

    public void setZXSJ(Timestamp ZXSJ) {
        this.ZXSJ = ZXSJ;
    }

    public int getDINDEX() {
        return DINDEX;
    }

    public int getEOID() {
        return EOID;
    }

    public int getOINDEX() {
        return OINDEX;
    }

    public String getSFQR() {
        return SFQR;
    }

    public int getXH() {
        return XH;
    }

    public String getCZDW() {
        return CZDW;
    }

    public String getCZNR() {
        return CZNR;
    }

    public String getJSR() {
        return JSR;
    }

    public String getLSDX() {
        return LSDX;
    }

    public String getSFWC() {
        return SFWC;
    }

    public String getXLR() {
        return XLR;
    }

    public String getZXR() {
        return ZXR;
    }

    public Timestamp getXLSJ() {
        return XLSJ;
    }

    public Timestamp getZXSJ() {
        return ZXSJ;
    }
}
