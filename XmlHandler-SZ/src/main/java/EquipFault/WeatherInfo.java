package EquipFault;

/**
 * Created by CaoYu on 2015/10/22.
 */
public class WeatherInfo {
    private String IssueTime;
    private String SignalType;
    private String SignalLevel;
    private String IssueState;
    private String District;

    public void setDistrict(String district) {
        District = district;
    }

    public String getIssueTime() {
        return IssueTime;
    }

    public void setIssueTime(String issueTime) {
        IssueTime = issueTime;
    }

    public String getSignalType() {
        return SignalType;
    }

    public void setSignalType(String signalType) {
        SignalType = signalType;
    }

    public String getSignalLevel() {
        return SignalLevel;
    }

    public void setIssueState(String issueState) {
        IssueState = issueState;
    }

    public String getIssueState() {
        return IssueState;
    }

    public void setSignalLevel(String signalLevel) {
        SignalLevel = signalLevel;
    }

    public String getDistrict() {
        return District;
    }
}
