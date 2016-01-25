package EquipFault;

import java.util.Date;

/**
 * Created by CaoYu on 2015/11/27.
 */
public class EquipStateAssessResult {
    private String LineName;
    private int VoltageLevel;
    private Date AssessTime;
    private String AssessResult;

    public void setAssessResult(String assessResult) {
        AssessResult = assessResult;
    }

    public void setAssessTime(Date assessTime) {
        AssessTime = assessTime;
    }

    public void setVoltageLevel(int voltageLevel) {
        VoltageLevel = voltageLevel;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public int getVoltageLevel() {
        return VoltageLevel;
    }

    public String getAssessResult() {
        return AssessResult;
    }

    public Date getAssessTime() {
        return AssessTime;
    }

    public String getLineName() {
        return LineName;
    }
}
