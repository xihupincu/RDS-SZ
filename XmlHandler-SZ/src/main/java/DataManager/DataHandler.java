package DataManager;

import EquipFault.*;
import dbManager.DbConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CaoYu on 2015/12/4.
 */
public class DataHandler {
    public static void main(String[] args){

        DataHandler dataHandler = new DataHandler();
        ArrayList lines = new ArrayList();

        ArrayList weather = new ArrayList();
        ArrayList important = new ArrayList();
        ArrayList realtime = new ArrayList();
        ArrayList stateassess = new ArrayList();
        ArrayList oprisk = new ArrayList();
        ArrayList dispatchrisktask = new ArrayList();
        ArrayList tasktable = new ArrayList();
        ArrayList dispatchrisksteprisktable = new ArrayList();
        ArrayList dispatchrisksteprisk = new ArrayList();
        ArrayList dispatchriskriskbar = new ArrayList();
        ArrayList dispatchcompsteprisktable = new ArrayList();
        ArrayList dispatchcompriskcompbar = new ArrayList();
        ArrayList dispatchcomptask = new ArrayList();
        ArrayList tasktable1 = new ArrayList();
        ArrayList tasktable2 = new ArrayList();

        dataHandler.GetImportantConsumer(important);
        dataHandler.GetWeatherInfos(weather);
        dataHandler.GetEquipStateAssessResult(stateassess);
        dataHandler.GetRealtimeFaultRate(realtime);
        dataHandler.GetOpRisk(oprisk);

        Map<String,ArrayList<String>> station2vitalloadList = new HashMap<String, ArrayList<String>>();
        for (ImportantConsumer importantConsumer:(ArrayList<ImportantConsumer>)important){
            if (station2vitalloadList.keySet().contains(importantConsumer.getStationName()))
                station2vitalloadList.get(importantConsumer.getStationName()).add(importantConsumer.getConsumerName());
            else {
                ArrayList<String> vitalloadList = new ArrayList<String>();
                vitalloadList.add(importantConsumer.getConsumerName());
                station2vitalloadList.put(importantConsumer.getStationName().replaceAll("站",""),vitalloadList);
            }

        }
        dataHandler.GetLineName(lines);
        dataHandler.GetDispatchRiskTask(dispatchrisktask);
        dataHandler.GetDispatchRiskTaskTable(tasktable);
        dataHandler.GetDispatchRiskStepRiskTable(dispatchrisksteprisktable,station2vitalloadList);
        dataHandler.GetDispatchRiskStepRisk(dispatchrisksteprisk);
        dataHandler.GetDispatchRiskRiskBar(dispatchriskriskbar);

        dataHandler.GetDispatchCompTask(dispatchcomptask);
        dataHandler.GetDispatchCompTaskTable1(tasktable1);
        dataHandler.GetDispatchCompTaskTable2(tasktable2);
        dataHandler.GetDispatchCompRiskCompBar(dispatchcompriskcompbar);
        dataHandler.GetDispatchCompStepRiskTable(dispatchcompsteprisktable,station2vitalloadList);

        int i = 2;
        i++;
    }



    public void GetImportantConsumer(ArrayList<ImportantConsumer> importantConsumerList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select STATIONNAME,CONSUMERNAME,IMPORTANCELEVEL,VOLTAGELEVEL,BUREAU from IMPORTANT_CONSUMER t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ImportantConsumer importantConsumer = new ImportantConsumer();
                importantConsumer.setStationName(rs.getString(1)) ;
                importantConsumer.setConsumerName(rs.getString(2));
                importantConsumer.setImportanceLevel(rs.getString(3));
                importantConsumer.setVoltageLevel(rs.getDouble(4));
                importantConsumer.setBureau(rs.getString(5));
                importantConsumerList.add(importantConsumer);
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

    public void GetWeatherInfos(ArrayList<WeatherInfo> weatherInfoList){
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

    public void GetRealtimeFaultRate(ArrayList<RealtimeFaultRate> realtimeFaultRateList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select ISSUETIME,LINENAME,REALTIMEFAULTRATE,MAINREASON,PROBABILITYLEVEL,VOLTAGELEVEL,DEVICETYPE from REALTIMEFAULTRATE t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                RealtimeFaultRate realtimeFaultRate = new RealtimeFaultRate();
                realtimeFaultRate.setIssueTime(rs.getTimestamp(1));
                realtimeFaultRate.setLineName(rs.getString(2));
                realtimeFaultRate.setRealtimeFaultRate(rs.getDouble(3));
                realtimeFaultRate.setMainReason(rs.getString(4));
                realtimeFaultRate.setProbabilityLevel(rs.getString(5));
                realtimeFaultRate.setVoltageLevel(rs.getDouble(6));
                realtimeFaultRate.setDeviceType(rs.getString(7));
                realtimeFaultRateList.add(realtimeFaultRate);
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

    public void GetEquipStateAssessResult(ArrayList<EquipStateAssessResult> equipStateAssessResultList){
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

    public void GetOpRisk(ArrayList<OpRisk> opRiskList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select RISKTYPE,RISKKIND,RISKLEVEL,REASONLEVEL,CONSEQLEVEL,RISKREASON,RISKCONSEQ,SJ,IMPORTANTCONSUMER,SEVERITY from OP_RISK t  ORDER BY  risktype DESC, severity DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                OpRisk opRisk = new OpRisk();
                opRisk.setRiskType(rs.getString(1));
                opRisk.setRiskKind(rs.getString(2));
                opRisk.setRiskLevel(rs.getInt(3));
                opRisk.setReasonLevel(rs.getInt(4));
                opRisk.setConseqLevel(rs.getInt(5));
                opRisk.setRiskReason(rs.getString(6));
                opRisk.setRiskConseq(rs.getString(7));
                opRisk.setAssessTime(rs.getTimestamp(8));
                opRisk.setImportantConsumer(rs.getString(9));
                opRisk.setSeverity(rs.getDouble(10));
                opRiskList.add(opRisk);
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

    public void GetDispatchRiskTask( ArrayList<Task> taskList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select SUCCESS,CZMD,CZSJ from DISPATCHRISK_TASK t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Task task = new Task();
                task.setSuccess(rs.getInt(1));
                task.setCZMD(rs.getString(2));
                task.setCZSJ(rs.getDate(3));
                taskList.add(task);
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

    public void GetDispatchRiskTaskTable( ArrayList<TaskTable> taskTableList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,UNIT,DETAIL from DISPATCHRISK_TASKTABLE t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                TaskTable taskTable = new TaskTable();
                taskTable.setOrder(rs.getInt(1));
                taskTable.setUnit(rs.getString(2));
                taskTable.setDetail(rs.getString(3));
                taskTableList.add(taskTable);
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

    public void GetDispatchRiskStepRiskTable( ArrayList<DispatchRiskStepRiskTable> dispatchRiskStepRiskTableList,Map<String,ArrayList<String>> station2vitalload){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,RISKTYPE,RISKLEVEL,PROBLEVEL,SEVLEVEL,RISKREASON,RISKQUE,RISKSEV from DISPATCHRISK_STEPRISKTABLE t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DispatchRiskStepRiskTable dispatchRiskStepRiskTable = new DispatchRiskStepRiskTable();
                dispatchRiskStepRiskTable.setMyorder(rs.getInt(1));
                dispatchRiskStepRiskTable.setRisktype(rs.getString(2));
                dispatchRiskStepRiskTable.setRisklevel(rs.getInt(3));
                dispatchRiskStepRiskTable.setProblevel(rs.getInt(4));
                dispatchRiskStepRiskTable.setSevlevel(rs.getInt(5));
                dispatchRiskStepRiskTable.setRiskreason(rs.getString(6));
                dispatchRiskStepRiskTable.setRiskque(rs.getString(7));
                dispatchRiskStepRiskTable.setRisksev(rs.getString(8));

                String vitalload = " ";
                if (dispatchRiskStepRiskTable.getRisktype().equals("厂站全停风险") || dispatchRiskStepRiskTable.getRisktype().equals("失负荷风险") ){
                    ArrayList<String> totalVitalLoad = new ArrayList<String>();
                    String riskQue = dispatchRiskStepRiskTable.getRiskque();
                    String[] stationVector = riskQue.split("-");
                    for (int i = 0;i < stationVector.length; i++){
                        String stationi = stationVector[i];
                        if (station2vitalload.keySet().contains(stationi.replaceAll("站",""))){
                            totalVitalLoad.addAll(station2vitalload.get(stationi.replaceAll("站","")));
                        }
                    }
                    for (String loadName : totalVitalLoad){
                        vitalload = vitalload + loadName +"-";
                    }
                }
                dispatchRiskStepRiskTable.setVitalload(vitalload);
                dispatchRiskStepRiskTableList.add(dispatchRiskStepRiskTable);
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

    public void GetDispatchRiskStepRisk( ArrayList<DispatchRiskStepRisk> dispatchRiskStepRiskList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,Statlosslevel,Loadlosslevel,Outputlosslevel,Overloadlevel,Lowvoltlevel,Highvoltlevel from DISPATCHRISK_STEPRISK t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DispatchRiskStepRisk dispatchRiskStepRisk = new DispatchRiskStepRisk();
                dispatchRiskStepRisk.setMyorder(rs.getInt(1));
                dispatchRiskStepRisk.setStalosslevel(rs.getInt(2));
                dispatchRiskStepRisk.setLoadlosslevel(rs.getInt(3));
                dispatchRiskStepRisk.setOutputlosslevel(rs.getInt(4));
                dispatchRiskStepRisk.setOverloadlevel(rs.getInt(5));
                dispatchRiskStepRisk.setLowvoltlevel(rs.getInt(6));
                dispatchRiskStepRisk.setHighvoltlevel(rs.getInt(7));
                dispatchRiskStepRiskList.add(dispatchRiskStepRisk);
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

    public void GetDispatchRiskRiskBar( ArrayList<DispatchRiskRiskBar> dispatchRiskRiskBarList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,Barlevel from DISPATCHRISK_RISKBAR t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DispatchRiskRiskBar dispatchRiskRiskBar = new DispatchRiskRiskBar();
                dispatchRiskRiskBar.setMyorder(rs.getInt(1));
                dispatchRiskRiskBar.setBarlevel(rs.getInt(2));
                dispatchRiskRiskBarList.add(dispatchRiskRiskBar);
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

    public void GetDispatchCompTask( ArrayList<Task> taskList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select SUCCESS,CZMD,CZSJ from DISPATCHCOMP_TASK t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Task task = new Task();
                task.setSuccess(rs.getInt(1));
                task.setCZMD(rs.getString(2));
                task.setCZSJ(rs.getDate(3));
                taskList.add(task);
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

    public void GetDispatchCompTaskTable1( ArrayList<TaskTable> taskTableList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,UNIT,DETAIL from DISPATCHCOMP_TASKTABLE1 t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                TaskTable taskTable = new TaskTable();
                taskTable.setOrder(rs.getInt(1));
                taskTable.setUnit(rs.getString(2));
                taskTable.setDetail(rs.getString(3));
                taskTableList.add(taskTable);
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

    public void GetDispatchCompTaskTable2( ArrayList<TaskTable> taskTableList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select MYORDER,UNIT,DETAIL from DISPATCHCOMP_TASKTABLE2 t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                TaskTable taskTable = new TaskTable();
                taskTable.setOrder(rs.getInt(1));
                taskTable.setUnit(rs.getString(2));
                taskTable.setDetail(rs.getString(3));
                taskTableList.add(taskTable);
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

    public void GetDispatchCompStepRiskTable( ArrayList<DispatchCompStepRiskTable> dispatchCompStepRiskTableList,Map<String,ArrayList<String>> station2vitalload){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select optype,myorder,Risktype,Risklevel ,Problevel, Sevlevel,  Riskreason ,Riskque ,Risksev from DISPATCHCOMP_STEPRISKTABLE t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DispatchCompStepRiskTable dispatchCompStepRiskTable = new DispatchCompStepRiskTable();
                dispatchCompStepRiskTable.setOptype(rs.getString(1));
                dispatchCompStepRiskTable.setMyorder(rs.getInt(2));
                dispatchCompStepRiskTable.setRisktype(rs.getString(3));
                dispatchCompStepRiskTable.setRisklevel(rs.getInt(4));
                dispatchCompStepRiskTable.setProblevel(rs.getInt(5));
                dispatchCompStepRiskTable.setSevlevel(rs.getInt(6));
                dispatchCompStepRiskTable.setRiskreason(rs.getString(7));
                dispatchCompStepRiskTable.setRiskque(rs.getString(8));
                dispatchCompStepRiskTable.setRisksev(rs.getString(9));
                String vitalload = " ";
                if (dispatchCompStepRiskTable.getRisktype().equals("厂站全停风险") || dispatchCompStepRiskTable.getRisktype().equals("失负荷风险") ){
                    ArrayList<String> totalVitalLoad = new ArrayList<String>();
                    String riskQue = dispatchCompStepRiskTable.getRiskque();
                    String[] stationVector = riskQue.split("-");
                    for (int i = 0;i < stationVector.length; i++){
                        String stationi = stationVector[i];
                        if (station2vitalload.keySet().contains(stationi.replaceAll("站",""))){
                            totalVitalLoad.addAll(station2vitalload.get(stationi.replaceAll("站","")));
                        }
                    }
                    for (String loadName : totalVitalLoad){
                        vitalload = vitalload + loadName +"-";
                    }
                }
                dispatchCompStepRiskTable.setVitalload(vitalload);
                dispatchCompStepRiskTableList.add(dispatchCompStepRiskTable);
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

    public void GetDispatchCompRiskCompBar( ArrayList<DispatchCompRiskCompBar> dispatchCompRiskCompBarList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select optype,Barlevel from DISPATCHCOMP_RiskCompBar t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DispatchCompRiskCompBar dispatchCompRiskCompBar = new DispatchCompRiskCompBar();
                dispatchCompRiskCompBar.setOptype(rs.getString(1));
                dispatchCompRiskCompBar.setBarlevel(rs.getInt(2));
                dispatchCompRiskCompBarList.add(dispatchCompRiskCompBar);
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

    public void GetLineName(ArrayList<String> lineNameList){
        Connection conn = null;
        String sql = null;
        try {
            conn = DbConnectionPool.getInstance().getConnection();
            sql = "select linename from Lines t";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lineNameList.add(rs.getString(1).replaceAll(" ",""));
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
