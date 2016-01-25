package dbManager;

import java.util.ResourceBundle;

public class ResourceManager {
    private static ResourceBundle dbaResource;
    private static ResourceBundle riskAssessPara;

    static {
        dbaResource = ResourceBundle.getBundle("config.dba");  //config目录下的dba.properties

//        riskAssessPara = ResourceBundle.getBundle("config.riskAssessPara");  //config目录下的riskAssessPara.properties
    }

    public static String getDriverClass() {
        return dbaResource.getString("dbservice.connection.driver_class");
    }
    public static String getDriverClass4WeatherInfos() {
        return dbaResource.getString("dbservice.connection.driver_class4WeatherInfos");
    }

    public static String getUrl() {
        return dbaResource.getString("dbservice.connection.url");
    }
    public static String getUrl4WeatherInfos() {
        return dbaResource.getString("dbservice.connection.url");
    }

    public static String getUsername() {
        return dbaResource.getString("dbservice.connection.username");
    }


    public static String getUsername4WeatherInfos() {
        return dbaResource.getString("dbservice.connection.username");
    }

    public static String getPassword() {
        return dbaResource.getString("dbservice.connection.password");
    }

    public static String getPassword4WeatherInfos() {
        return dbaResource.getString("dbservice.connection.password");
    }

    public static int getPoolSize() {
        return Integer.valueOf(dbaResource.getString("dbservice.connection.poolSize"));
    }

    public static double getRaFacadeOverLoadLim() {
        return Double.valueOf(riskAssessPara.getString("rafacade.overloadlim"));
    }

    public static double getRaFacadeOverLoadCoeff() {
        return Double.valueOf(riskAssessPara.getString("rafacade.overloadcoeff"));
    }

    public static double getRaFacadeLowVoltageLim() {
        return Double.valueOf(riskAssessPara.getString("rafacade.lowvoltagelim"));
    }

    public static double getRaFacadeLowVoltageCoeff() {
        return Double.valueOf(riskAssessPara.getString("rafacade.lowvoltagecoeff"));
    }

    public static double getRaFacadehighVoltageLim() {
        return Double.valueOf(riskAssessPara.getString("rafacade.highvoltagelim"));
    }

    public static double getRaFacadehighVoltageCoeff() {
        return Double.valueOf(riskAssessPara.getString("rafacade.highvoltagecoeff"));
    }

    public static double getFaultProbImpossible() {
        return Double.valueOf(riskAssessPara.getString("riskAssessRiskLevelResult.faultprob.impossible"));
    }

    public static double getFaultProbVeryLow() {
        return Double.valueOf(riskAssessPara.getString("riskAssessRiskLevelResult.faultprob.verylow"));
    }

    public static double getFaultProbLow() {
        return Double.valueOf(riskAssessPara.getString("riskAssessRiskLevelResult.faultprob.low"));
    }

    public static double getFaultProbMedium() {
        return Double.valueOf(riskAssessPara.getString("riskAssessRiskLevelResult.faultprob.medium"));
    }

    public static void refresh() {
        dbaResource = ResourceBundle.getBundle("config.dba");
        riskAssessPara = ResourceBundle.getBundle("config.riskAssessPara");
    }

    public static void main(String[] args) {
        new ResourceManager();
    }
}