package EquipFault;

import java.sql.Timestamp;

/**
 * Created by CaoYu on 2015/10/23.
 */
public class RealtimeFaultRate {
    private Timestamp IssueTime;
    private String LineName;
    private double RealtimeFaultRate;
    private String MainReason;
    private double WeatherFaultRate;
    private double AccidentFaultRate;
    private String ProbabilityLevel;
    private String DeviceType;
    private double VoltageLevel;

    public void setVoltageLevel(double voltageLevel) {
        VoltageLevel = voltageLevel;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public double getVoltageLevel() {
        return VoltageLevel;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setIssueTime(Timestamp issueTime) {
        IssueTime = issueTime;
    }

    public Timestamp getIssueTime() {
        return IssueTime;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getLineName() {
        return LineName;
    }

    public void setRealtimeFaultRate(double realtimeFaultRate) {
        RealtimeFaultRate = realtimeFaultRate;
    }

    public double getRealtimeFaultRate() {
        return RealtimeFaultRate;
    }

    public void setMainReason(String mainReason) {
        MainReason = mainReason;
    }

    public String getMainReason() {
        return MainReason;
    }

    public void setAccidentFaultRate(double accidentFaultRate) {
        AccidentFaultRate = accidentFaultRate;
    }

    public double getAccidentFaultRate() {
        return AccidentFaultRate;
    }

    public void setWeatherFaultRate(double weatherFaultRate) {
        WeatherFaultRate = weatherFaultRate;
    }

    public double getWeatherFaultRate() {
        return WeatherFaultRate;
    }

    public void setProbabilityLevel(String probabilityLevel) {
        ProbabilityLevel = probabilityLevel;
    }

    public String getProbabilityLevel() {
        return ProbabilityLevel;
    }
}
