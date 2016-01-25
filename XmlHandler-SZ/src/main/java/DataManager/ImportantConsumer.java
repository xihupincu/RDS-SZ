package DataManager;

/**
 * Created by CaoYu on 2015/12/2.
 */
public class ImportantConsumer {
    private String stationName;
    private String consumerName;
    private String importanceLevel;
    private double voltageLevel;
    private String bureau;

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public void setImportanceLevel(String importantLevel) {
        this.importanceLevel = importantLevel;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setVoltageLevel(double voltageLevel) {
        this.voltageLevel = voltageLevel;
    }

    public double getVoltageLevel() {
        return voltageLevel;
    }

    public String getBureau() {
        return bureau;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public String getStationName() {
        return stationName;
    }
}
