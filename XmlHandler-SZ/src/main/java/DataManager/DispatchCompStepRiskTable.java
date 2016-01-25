package DataManager;

/**
 * Created by CaoYu on 2015/12/5.
 */
public class DispatchCompStepRiskTable {
    private String optype;
    private int myorder;
    private String risktype;
    private int risklevel;
    private int problevel;
    private int sevlevel;
    private String riskreason;
    private String riskque;
    private String risksev;
    private String vitalload;

    public String getVitalload() {
        return vitalload;
    }

    public void setVitalload(String vitalload) {
        this.vitalload = vitalload;
    }

    public void setMyorder(int myorder) {
        this.myorder = myorder;
    }

    public int getMyorder() {
        return myorder;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public void setProblevel(int problevel) {
        this.problevel = problevel;
    }

    public void setRisklevel(int risklevel) {
        this.risklevel = risklevel;
    }

    public void setRiskque(String riskque) {
        this.riskque = riskque;
    }

    public void setRiskreason(String riskreason) {
        this.riskreason = riskreason;
    }

    public void setRisksev(String risksev) {
        this.risksev = risksev;
    }

    public void setRisktype(String risktype) {
        this.risktype = risktype;
    }

    public void setSevlevel(int sevlevel) {
        this.sevlevel = sevlevel;
    }

    public int getProblevel() {
        return problevel;
    }

    public int getRisklevel() {
        return risklevel;
    }

    public int getSevlevel() {
        return sevlevel;
    }

    public String getOptype() {
        return optype;
    }

    public String getRiskque() {
        return riskque;
    }

    public String getRiskreason() {
        return riskreason;
    }

    public String getRisksev() {
        return risksev;
    }

    public String getRisktype() {
        return risktype;
    }
}
