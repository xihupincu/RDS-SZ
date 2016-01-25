package EquipFault;

/**
 * Created by CaoYu on 2015/10/20.
 */
public class FaultRate {
    private String LineName;
    private double Voltage;
    private double WeatherFaultRate;
    private double AccidentFaultRate;
    private int MinYear;
    private int MaxYear;

    public void  setLineName(String name){this.LineName = name;}

    public void setVoltage(double voltage){this.Voltage = voltage;}

    public void setWeatherFaultRate(double weatherFaultRate){this.WeatherFaultRate = weatherFaultRate;}

    public void setAccidentFaultRate(double accidentFaultRate){this.AccidentFaultRate = accidentFaultRate;}

    public void setMinYear(int year){this.MinYear = year;}

    public void setMaxYear(int year){this.MaxYear = year;}

    public String getLineName(){return LineName;}

    public double getVoltage(){return Voltage; }

    public double getWeatherFaultRate(){return  WeatherFaultRate;}

    public double getAccidentFaultRate(){return  AccidentFaultRate;}

    public int getMinYear(){return MinYear;}

    public int getMaxYear(){return MaxYear;}

}
