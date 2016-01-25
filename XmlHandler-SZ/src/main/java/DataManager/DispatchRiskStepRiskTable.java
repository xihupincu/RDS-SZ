package DataManager;

/**
 * Created by CaoYu on 2015/12/4.
 */
public class DispatchRiskStepRiskTable {
    private int myorder;
    private String risktype;
    private int risklevel;
    private int problevel;
    private int sevlevel;
    private String riskreason;
    private String riskque;
    private String risksev;
    private String vitalload;

    public void setVitalload(String vitalload) {
        this.vitalload = vitalload;
    }

    public String getVitalload() {
        return vitalload;
    }

    public void setMyorder(int myorder) {
        this.myorder = myorder;
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

    public int getMyorder() {
        return myorder;
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
