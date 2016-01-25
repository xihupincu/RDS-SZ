package DataManager;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by CaoYu on 2015/12/2.
 */
public class OpRisk {
    private String riskType;
    private String riskKind;
    private int riskLevel;
    private int reasonLevel;
    private int conseqLevel;
    private String riskReason;
    private String riskConseq;
    private java.sql.Timestamp assessTime;
    private String importantConsumer;
    private double severity;
    private int isMax ;

    public void setIsMax(int isMax) {
        this.isMax = isMax;
    }

    public int getIsMax() {
        return isMax;
    }

    public void setSeverity(double severity) {
        this.severity = severity;
    }

    public void setAssessTime(Timestamp assessTime) {
        this.assessTime = assessTime;
    }

    public void setConseqLevel(int conseqLevel) {
        this.conseqLevel = conseqLevel;
    }

    public void setImportantConsumer(String importantConsumer) {
        this.importantConsumer = importantConsumer;
    }

    public void setReasonLevel(int reasonLevel) {
        this.reasonLevel = reasonLevel;
    }

    public void setRiskConseq(String riskConseq) {
        this.riskConseq = riskConseq;
    }

    public void setRiskKind(String riskKind) {
        this.riskKind = riskKind;
    }

    public void setRiskLevel(int riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setRiskReason(String riskReason) {
        this.riskReason = riskReason;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public int getConseqLevel() {
        return conseqLevel;
    }

    public int getReasonLevel() {
        return reasonLevel;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public Timestamp getAssessTime() {
        return assessTime;
    }

    public String getImportantConsumer() {
        return importantConsumer;
    }

    public String getRiskConseq() {
        return riskConseq;
    }

    public String getRiskKind() {
        return riskKind;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public String getRiskType() {
        return riskType;
    }

    public double getSeverity() {
        return severity;
    }

}
