package EquipFault;

import _61970.wires.ACLineSegment;
import dbManager.DbConnectionPool;

import zju.ieeeformat.BranchData;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CaoYu on 2015/10/20.
 */
public class EquipFaultHandler {
    private int MinYear=2011;
    private int MaxYear=2015;

    public static void main(String[] args){
//        String a = "李丽仪悦1";
//        String b = "李丽仪1";


        EquipFaultHandler equipFaultHandler = new EquipFaultHandler();
        List list = new ArrayList();
        equipFaultHandler.UpdateRealtimeFaultRate();
        equipFaultHandler.UpdateFaultRate();

    }
    public  void UpdateFaultRate(){

        List<FaultData> faultDataList=new ArrayList<FaultData>();
        GetFaultDatas(faultDataList);
        List<FaultRate> faultRateList = new ArrayList<FaultRate>();
        CalculateFaultRates(faultDataList, faultRateList);
        SendFaultRates(faultRateList);
        int i=0;
        i++;


    }

    public void UpdateRealtimeFaultRate(){
        List<FaultRate> faultRateList = new ArrayList<FaultRate>();
        List<WeatherInfo> weatherInfoList = new ArrayList<WeatherInfo>();
        List<RealtimeFaultRate> realtimeFaultRateList = new ArrayList<RealtimeFaultRate>();
        List<EquipStateAssessResult>equipStateAssessResultList = new ArrayList<EquipStateAssessResult>();
        GetFaultRates(faultRateList);
        GetWeatherInfos(weatherInfoList);
        GetEquipStateAssessResult(equipStateAssessResultList);


        CalculateRealFaultRates(weatherInfoList,faultRateList,equipStateAssessResultList,realtimeFaultRateList);
        SendRealFaultRates(realtimeFaultRateList);
        int i=0;
        i++;
    }

    public void SendRealFaultRates(List<RealtimeFaultRate> realtimeFaultRateList){
        Connection conn = null;
        String sql = null;
        PreparedStatement pstmt = null;
        try {

            conn = DbConnectionPool.getInstance().getConnection();
            //清除历史数据
            sql = "delete from REALTIMEFAULTRATE";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            for (RealtimeFaultRate realtimeFaultRate : realtimeFaultRateList) {
                sql = "insert into REALTIMEFAULTRATE (ISSUETIME,LINENAME,REALTIMEFAULTRATE,MAINREASON,WEATHERFAULTRATE,ACCIDENTFAULTRATE,PROBABILITYLEVEL,VOLTAGELEVEL,DEVICETYPE) values (?,?,?,?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setTimestamp(1, realtimeFaultRate.getIssueTime());
                pstmt.setString(2,realtimeFaultRate.getLineName());
                pstmt.setDouble(3,realtimeFaultRate.getRealtimeFaultRate());
                pstmt.setString(4,realtimeFaultRate.getMainReason());
                pstmt.setDouble(5,realtimeFaultRate.getWeatherFaultRate());
                pstmt.setDouble(6,realtimeFaultRate.getAccidentFaultRate());
                pstmt.setString(7,realtimeFaultRate.getProbabilityLevel());
                pstmt.setDouble(8,realtimeFaultRate.getVoltageLevel());
                pstmt.setString(9,realtimeFaultRate.getDeviceType());
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void SendFaultRates(List<FaultRate> faultRateList){

        Connection conn = null;
        String sql = null;
        PreparedStatement pstmt = null;
        try {

            conn = DbConnectionPool.getInstance().getConnection();

            //清除历史数据
            sql = "delete from EQU_FAULT_RATE";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();

            for (FaultRate faultRate : faultRateList) {
                sql = "insert into EQU_FAULT_RATE (LINENAME,VOLTAGE,WEATHERFAULTRATE,ACCIDENTFAULTRATE,MINYEAR,MAXYEAR) values (?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, faultRate.getLineName());
                pstmt.setDouble(2, faultRate.getVoltage());
                pstmt.setDouble(3, faultRate.getWeatherFaultRate());
                pstmt.setDouble(4, faultRate.getAccidentFaultRate());
                pstmt.setInt(5, faultRate.getMinYear());
                pstmt.setInt(6, faultRate.getMaxYear());
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void CalculateRealFaultRates(List<WeatherInfo> weatherInfoList, List<FaultRate> faultRateList, List<EquipStateAssessResult> equipStateAssessResultList,List<RealtimeFaultRate> realtimeFaultRateList){
        //TODO： 这两个规则后续要从数据库读入
       Map<String, Double> WeatherRuleMap = new HashMap<String, Double>();
        WeatherRuleMap.put("雷电，蓝色", 1.2);
        WeatherRuleMap.put("雷电，黄色", 1.6);
        WeatherRuleMap.put("雷电，橙色", 2.0);
        WeatherRuleMap.put("雷电，红色", 3.0);
        WeatherRuleMap.put("大风，蓝色", 1.1);
        WeatherRuleMap.put("大风，黄色", 1.2);
        WeatherRuleMap.put("大风，橙色", 1.6);
        WeatherRuleMap.put("大风，红色", 2.0);
        WeatherRuleMap.put("暴雨，蓝色", 1.1);
        WeatherRuleMap.put("暴雨，黄色", 1.2);
        WeatherRuleMap.put("暴雨，橙色", 1.6);
        WeatherRuleMap.put("暴雨，红色", 2.0);
        WeatherRuleMap.put("火险，蓝色", 1.2);
        WeatherRuleMap.put("火险，黄色", 1.4);
        WeatherRuleMap.put("火险，橙色", 1.6);
        WeatherRuleMap.put("火险，红色", 1.8);
        WeatherRuleMap.put("高温，蓝色", 1.1);
        WeatherRuleMap.put("高温，黄色", 1.2);
        WeatherRuleMap.put("高温，橙色", 1.4);
        WeatherRuleMap.put("高温，红色", 1.5);
        WeatherRuleMap.put("台风，蓝色", 1.2);
        WeatherRuleMap.put("台风，黄色", 2.0);
        WeatherRuleMap.put("台风，橙色", 3.0);
        WeatherRuleMap.put("台风，红色", 4.0);
        WeatherRuleMap.put("灰霾，蓝色", 1.1);
        WeatherRuleMap.put("灰霾，黄色", 1.2);
        WeatherRuleMap.put("灰霾，橙色", 1.6);
        WeatherRuleMap.put("灰霾，红色", 2.0);
        WeatherRuleMap.put("寒冷，蓝色", 1.1);
        WeatherRuleMap.put("寒冷，黄色", 1.2);
        WeatherRuleMap.put("寒冷，橙色", 1.6);
        WeatherRuleMap.put("寒冷，红色", 2.0);


        double[] ProbabilityLevelRuleMap = new double[]{0.5,2,5,7,};

//----------------------------------------------------------------------------

        List<WeatherInfo> ThunderList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> RainstormList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> WindList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> FireList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> HightemperatureList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> HazeList = new ArrayList<WeatherInfo>();
        List<WeatherInfo> ColdList = new ArrayList<WeatherInfo>();
        for (WeatherInfo weatherInfo:weatherInfoList){
            if (weatherInfo.getSignalType().equals("雷电"))
                ThunderList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("暴雨"))
                RainstormList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("大风") || weatherInfo.getSignalType().equals("台风"))
                WindList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("火险"))
                FireList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("高温"))
                HightemperatureList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("灰霾"))
                HazeList.add(weatherInfo);
            else if (weatherInfo.getSignalType().equals("寒冷"))
                ColdList.add(weatherInfo);
        }
        List<WeatherInfo> ValidWeatherInfoList = new ArrayList<WeatherInfo>();
        if (ThunderList.size()!=0){
            if (ThunderList.get(ThunderList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(ThunderList.get(ThunderList.size()-1));
        }
        if (RainstormList.size()!=0){
            if (RainstormList.get(RainstormList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(RainstormList.get(RainstormList.size()-1));
        }
        if (WindList.size()!=0){
            if (WindList.get(WindList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(WindList.get(WindList.size()-1));
        }
        if (FireList.size()!=0){
            if (FireList.get(FireList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(FireList.get(FireList.size()-1));
        }
        if (HazeList.size()!=0){
            if (HazeList.get(HazeList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(HazeList.get(HazeList.size()-1));
        }
        if (ColdList.size()!=0){
            if (ColdList.get(ColdList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(ColdList.get(ColdList.size()-1));
        }
        if (HightemperatureList.size()!=0){
            if (HightemperatureList.get(HightemperatureList.size()-1).getIssueState().equals("发布"))
                ValidWeatherInfoList.add(HightemperatureList.get(HightemperatureList.size()-1));
        }
        double WeatherFactor = 0;
        if (ValidWeatherInfoList.size()>0){
            for (WeatherInfo weatherInfo:ValidWeatherInfoList){
                String Keyword = new String();
                if (weatherInfo.getSignalLevel().equals(" "))
                    Keyword = weatherInfo.getSignalType()+"，"+"蓝色";
                else
                    Keyword = weatherInfo.getSignalType()+"，"+weatherInfo.getSignalLevel();
                WeatherFactor += WeatherRuleMap.get(Keyword);
            }
        }
        else {WeatherFactor = 1;}

        for (FaultRate faultRate:faultRateList){
            RealtimeFaultRate realtimeFaultRate = new RealtimeFaultRate();
            realtimeFaultRate.setLineName(faultRate.getLineName());
            realtimeFaultRate.setDeviceType("线路");
            realtimeFaultRate.setVoltageLevel(faultRate.getVoltage());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            double EquipStateAssessFactor = 1;
            String EquipStateAssess = "未评价";
            for (EquipStateAssessResult equipStateAssessResult:equipStateAssessResultList){
                if (realtimeFaultRate.getLineName().contains(equipStateAssessResult.getLineName()) || equipStateAssessResult.getLineName().contains(realtimeFaultRate.getLineName())){
                    if (equipStateAssessResult.getAssessResult().equals("正常")){
                        EquipStateAssessFactor = 1;
                        EquipStateAssess = equipStateAssessResult.getAssessResult();
                    }

                    else if (equipStateAssessResult.getAssessResult().equals("注意")){
                        EquipStateAssessFactor = 1.2;
                        EquipStateAssess = equipStateAssessResult.getAssessResult();
                    }
                    else if (equipStateAssessResult.getAssessResult().equals("异常")){
                        EquipStateAssessFactor = 1.5;
                        EquipStateAssess = equipStateAssessResult.getAssessResult();
                    }
                    else if (equipStateAssessResult.getAssessResult().equals("严重")){
                        EquipStateAssessFactor = 2;
                        EquipStateAssess = equipStateAssessResult.getAssessResult();
                    }
                }
            }
//            String CalculateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            realtimeFaultRate.setIssueTime(timestamp);
            realtimeFaultRate.setWeatherFaultRate(faultRate.getWeatherFaultRate());
            realtimeFaultRate.setAccidentFaultRate(faultRate.getAccidentFaultRate());
            double RealtimeFaultRate = (realtimeFaultRate.getAccidentFaultRate()+realtimeFaultRate.getWeatherFaultRate())*0.7*WeatherFactor;
            String mainreason = "(1)设备历史故障率为"+String.valueOf(faultRate.getAccidentFaultRate()+faultRate.getWeatherFaultRate())+"次/年。";
//            mainreason += "，其中①偶然事件（人为、外飘物等）造成的历史故障率为"+String.valueOf(faultRate.getAccidentFaultRate())+"次/年，";
//            mainreason += "②天气因素造成的历史故障率为" + String.valueOf(faultRate.getWeatherFaultRate())+"次/年。";
            mainreason += "(2)设备状态评价因数为" + String.valueOf(EquipStateAssessFactor)+"," + "设备状态总体评价为"+ "'"+EquipStateAssess + "'。";
            mainreason += "(3)天气影响因数为"+String.valueOf(WeatherFactor);
            mainreason +=",其中含：";
            for (WeatherInfo weatherInfo:ValidWeatherInfoList){
                String Keyword = new String();
                if (weatherInfo.getSignalLevel().equals(" "))
                    Keyword = weatherInfo.getSignalType()+"蓝色预警";
                else
                    Keyword = weatherInfo.getSignalType()+weatherInfo.getSignalLevel()+"预警，";
                mainreason +=Keyword;
            }
            mainreason.substring(0,mainreason.length()-1);
            mainreason += "。";
            realtimeFaultRate.setMainReason(mainreason);
            realtimeFaultRate.setRealtimeFaultRate(RealtimeFaultRate);
            if (RealtimeFaultRate < ProbabilityLevelRuleMap[0])
                realtimeFaultRate.setProbabilityLevel("可能性很小");
            else if (RealtimeFaultRate < ProbabilityLevelRuleMap[1])
                realtimeFaultRate.setProbabilityLevel("可能性较小");
            else if (RealtimeFaultRate < ProbabilityLevelRuleMap[2])
                realtimeFaultRate.setProbabilityLevel("可能性一般");
            else if (RealtimeFaultRate < ProbabilityLevelRuleMap[3])
                realtimeFaultRate.setProbabilityLevel("可能性较大");
            else
                realtimeFaultRate.setProbabilityLevel("可能性很大");
            realtimeFaultRateList.add(realtimeFaultRate);
        }

    }

    public void CalculateFaultRates( List<FaultData> faultDataList,List<FaultRate> faultRateList){
        Map<String,List<FaultData>> Name2FaultDatas = new HashMap<String, List<FaultData>>();
        for (FaultData faultData:faultDataList){
            String name1 = faultData.getLineName().replaceAll(" ","");
            Boolean flag = false;
            for (String name:Name2FaultDatas.keySet()){
                if (name1.contains(name)){
                    Name2FaultDatas.get(name).add(faultData);
                    name = name1;
                    flag = true;
                }
                else if (name.contains(name1)){
                    Name2FaultDatas.get(name).add(faultData);
                    flag = true;
                }
            }
            if (flag == false){
                List<FaultData> temp = new ArrayList<FaultData>();
                temp.add(faultData);
                Name2FaultDatas.put(name1,temp);
            }
        }
        for (String name:Name2FaultDatas.keySet()){
            double total_fault=0;
            FaultRate faultRate = new FaultRate();
            faultRate.setLineName(name);
            faultRate.setVoltage(Name2FaultDatas.get(name).get(0).getVoltage());
            List<FaultData> WeatherFaultData = new ArrayList<FaultData>();
            List<FaultData> AccidentFaultData = new ArrayList<FaultData>();

            for (FaultData faultData:Name2FaultDatas.get(name)){
                MaxYear= MaxYear>faultData.getFaultYear()? MaxYear:faultData.getFaultYear();
                MinYear = MinYear<faultData.getFaultYear()? MinYear:faultData.getFaultYear();

                if (faultData.getFaultType().equals("外飘物") || faultData.getFaultType().equals("人为") || faultData.getFaultType().equals("保护动作") || faultData.getFaultType().equals("内部故障") || faultData.getFaultType().equals("其他") )
                    AccidentFaultData.add(faultData);
                else if (faultData.getFaultType().equals("雷击") || faultData.getFaultType().equals("风偏") || faultData.getFaultType().equals("台风") || faultData.getFaultType().equals("山火") || faultData.getFaultType().equals("降雨"))
                    WeatherFaultData.add(faultData);
            }
            faultRate.setMinYear(MinYear);
            faultRate.setMaxYear(MaxYear);
            //设置天气故障率
            for (FaultData faultData:WeatherFaultData){
                total_fault += faultData.getFaultNum();
            }
            faultRate.setWeatherFaultRate(total_fault/(MaxYear-MinYear+1));
            //设置偶然故障率
            for (FaultData faultData:AccidentFaultData){
                total_fault += faultData.getFaultNum();
            }
            faultRate.setAccidentFaultRate(total_fault/(MaxYear-MinYear+1));
            faultRateList.add(faultRate);
        }
    }



    public void GetFaultRates(List<FaultRate> faultRateList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select LINENAME,VOLTAGE,WEATHERFAULTRATE,ACCIDENTFAULTRATE,MINYEAR,MAXYEAR from EQU_FAULT_RATE t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                FaultRate faultRate = new FaultRate();
                faultRate.setLineName(rs.getString(1)) ;
                faultRate.setVoltage( rs.getDouble(2));
                faultRate.setWeatherFaultRate(rs.getDouble(3));
                faultRate.setAccidentFaultRate(rs.getDouble(4));
                faultRate.setMinYear(rs.getInt(5));
                faultRate.setMaxYear(rs.getInt(6));
                faultRateList.add(faultRate);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void GetFaultDatas(List<FaultData> faultDatas){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select NAME_LINE,VOLTAGE,TYPE_FAULT,NUM_FAULT,YEAR_FAULT from EQU_FAULT_DATA t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                FaultData faultData = new FaultData();
                faultData.setLineName(rs.getString(1)) ;
                faultData.setVoltage( rs.getDouble(2));
                faultData.setFaultType( rs.getString(3));
                faultData.setFaultNum( rs.getDouble(4));
                faultData.setFaultYear(rs.getInt(5));
                faultDatas.add(faultData);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void GetWeatherInfos(List<WeatherInfo> weatherInfoList){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String insertTime = sdf.format(timestamp);
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection4WeatherInfos();
//            sql = "select ISSUETIME,SIGNALTYPE,SIGNALLEVEL,ISSUESTATE from SIGNALSYSINFO where  ISSUETIME < (to_date('" + insertTime + "','yyyy/mm/dd hh24:mi:ss')) and ISSUETIME > (to_date('" + insertTime + "','yyyy/mm/dd hh24:mi:ss')- interval '30' day ) ";//todo:修改成当前时间以前24小时内的预警
            sql = "select ISSUETIME,SIGNALTYPE,SIGNALLEVEL,ISSUESTATE,DISTRICT from SIGNALSYSINFO where  ISSUETIME < (to_date('2015/12/17 00:00:00','yyyy/mm/dd hh24:mi:ss')) and ISSUETIME > (to_date('2015/12/17 00:00:00','yyyy/mm/dd hh24:mi:ss')- interval '16' day ) ";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setIssueTime(sdf.format(rs.getTimestamp(1)));
                weatherInfo.setSignalType(rs.getString(2));
                weatherInfo.setSignalLevel(rs.getString(3));
                if (weatherInfo.getSignalLevel().equals(" "))
                    weatherInfo.setSignalLevel("蓝色");
                weatherInfo.setIssueState(rs.getString(4));
                weatherInfo.setDistrict(rs.getString(5));
                weatherInfoList.add(weatherInfo);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void GetEquipStateAssessResult(List<EquipStateAssessResult> equipStateAssessResultList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select LINENAME,VOLTAGELEVEL,ASSESSTIME,ASSESSRESULT from EQU_STATE_DATA t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                EquipStateAssessResult equipStateAssessResult = new EquipStateAssessResult();
                equipStateAssessResult.setLineName(rs.getString(1)) ;
                equipStateAssessResult.setVoltageLevel(rs.getInt(2));
                equipStateAssessResult.setAssessTime(rs.getDate(3));
                equipStateAssessResult.setAssessResult(rs.getString(4));
                equipStateAssessResultList.add(equipStateAssessResult);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DbConnectionPool.getInstance().release(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
