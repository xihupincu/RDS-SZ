package EquipFault;

/**
 * Created by CaoYu on 2015/10/20.
 */
public class FaultData {
    private String LineName;
    private double Voltage;
    private String FaultType;
    private double FaultNum;
    private int FaultYear;

    public void setLineName(String name){this.LineName=name;}

    public void setVoltage(double voltage){this.Voltage=voltage;}

    public void setFaultType(String faultType){this.FaultType=faultType;}

    public void setFaultNum(double faultNum){this.FaultNum=faultNum;}

    public void setFaultYear(int faultYear){this.FaultYear=faultYear;}

    public String getLineName(){return LineName;}

    public double getVoltage(){return Voltage; }

    public String getFaultType(){return  FaultType;}

    public double getFaultNum(){return  FaultNum;}

    public int getFaultYear(){return  FaultYear;}
}
