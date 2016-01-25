package FunctionClass;

import DataManager.*;
import DataManager.Task;
import EquipFault.*;
import ReadXml.*;
import WriteXml.WriteXML;
import dbManager.DbConnectionPool;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CaoYu on 2015/12/2.
 */
public class XmlHandler {
    private static String inputFile = "g:/students.xml";
    private static String outputFile = "g:/output.xml";
    private  int ticketType = 0;

    public static void main(String[] args) {
        long timeMillis = System.currentTimeMillis();
        XmlHandler xmlHandler = new XmlHandler();
//        DispatchOperation dispatchOperation = xmlHandler.readDataFromXML("f:/ticket/20151218213952493.xml");
//        xmlHandler.writeDataToDB(dispatchOperation);
        xmlHandler.writeDataToXML("F:/",timeMillis);

        System.out.println("耗时：" + (System.currentTimeMillis() - timeMillis) + "ms.");
    }
    public DispatchOperation readDataFromXML(String inputFile){
        DispatchOperation dispatchOperation = new DispatchOperation();

        try {
            List<EventFactory.InternalEvent> events = new ArrayList<EventFactory.InternalEvent>();
            events = new EventFactory().read(inputFile);
            ArrayList<TaskItems> taskItemsArrayList = new ArrayList<TaskItems>();
            dispatchOperation.setTaskItemsArrayList(taskItemsArrayList);
            for (EventFactory.InternalEvent event:events){
                if (event.getElementName().equals("DispatchOperation")){
                    dispatchOperation.setName(event.getProp("Name"));
                    dispatchOperation.setGenTime(event.getProp("GenTime"));
                    dispatchOperation.setReceiver(event.getProp("Receiver"));
                    dispatchOperation.setVender(event.getProp("Vender"));
                }
                else if (event.getElementName().equals("Task")){
                    if (event.getProps().keySet().contains("CZMD") ){
                        ReadXml.Task task = new ReadXml.Task();
                        task.setCZMD(event.getProp("CZMD"));
                        String NPTTIME = event.getProp("NPTIME");
                        if (!NPTTIME.equals(""))
                             task.setNPTTIME(Timestamp.valueOf(NPTTIME));
                        else
                            task.setNPTTIME(Timestamp.valueOf(dispatchOperation.getGenTime()));
                        dispatchOperation.setTask(task);
                    }else {
                        ticketType = -1;
                    }
                }else if (event.getElementName().equals("TaskItems")){
                    if (event.getProps().keySet().contains("CZDW") && event.getProps().keySet().contains("CZNR") && event.getProps().keySet().contains("DINDEX")){
                        TaskItems taskItems = new TaskItems();
                        taskItems.setCZDW(event.getProp("CZDW"));
                        taskItems.setCZNR(event.getProp("CZNR"));
                        taskItems.setDINDEX(Integer.valueOf(event.getProp("DINDEX")));
                        taskItemsArrayList.add(taskItems);
                    }else {
                        ticketType = -1;
                    }
                }

            }

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("操作票解析错误！");
        }
        return dispatchOperation;
    }
    public void writeDataToDB( DispatchOperation dispatchOperation){
        if ( ticketType == -1){
            send2DB(0,null,null,null);
        }else {
        ReadXml.Task task = dispatchOperation.getTask();
        String regEx_XL = "([0-9]*kV)([^0-9]*线)[^0-9]*([0-9]*)[^0-9]*(由运行转检修)";
        Pattern pattern_XL = Pattern.compile(regEx_XL);
        String regEx_HS = "(合上)([0-9]*kV)(.*线)([^0-9]*)([0-9]*接地刀闸)";
        Pattern pattern_HS = Pattern.compile(regEx_HS);
        String regEx_ZBJX = "(.*站)(.*)(#[0-9]主变)(由运行转检修)";
        Pattern pattern_ZBJX = Pattern.compile(regEx_ZBJX);
        String regEx_DMX = "([0-9]*kV)([^0-9]*线)([0-9]*开关)(由)(.*M)(倒至)(.*M)(.*)";
        Pattern pattern_DMX = Pattern.compile(regEx_DMX);
        String regEx_RD =  "([0-9]*kV)([^0-9]*线)([0-9]*开关)(由)(.*M)(热倒至)(.*M)(.*)";
        Pattern pattern_RD = Pattern.compile(regEx_RD);
        String regEx_LD = "([0-9]*kV)([^0-9]*线)([0-9]*开关)(由)(.*M)(冷倒至)(.*M)(.*)";
        Pattern pattern_LD = Pattern.compile(regEx_LD);
        List<Ticket> ticketList = new ArrayList<Ticket>();



//        String str = "220kV皇滨乙线(2773)由运行转检修";
//        str = "220kV简坪甲线(2313)线路及坪山站开关由运行转检修";



           String CZMD = task.getCZMD().replaceAll(" ", "");
            if (CZMD.contains("倒至") && CZMD.contains("母线")){
                CZMD = CZMD.replaceAll("母线","");
            }
           if ( ticketType == 0){
               Matcher matcher = pattern_XL.matcher(CZMD);
               if (matcher.find()){
                   ticketType = 1;
                   String unit = (matcher.group(2)+matcher.group(3)).replaceAll(" ", "");
                   String detail = "线路运行转检修";
                   Ticket ticket0 = new Ticket();
                   ticket0.setMyorder(0);
                   ticket0.setUnit(unit);
                   ticket0.setDetail(detail);
                   ticketList.add(ticket0);

                   for (TaskItems taskItems:dispatchOperation.getTaskItemsArrayList()){
                       Matcher matcher1 = pattern_HS.matcher(taskItems.getCZNR().replaceAll(" ", ""));
                       if (matcher1.find()){
                           String detail1 = matcher1.group(0);
                           if (detail1.contains("线路侧"))
                               detail1 = detail1.replace("线路侧","");
                           Ticket ticket = new Ticket();
                           ticket.setMyorder(taskItems.getDINDEX());

                           ticket.setUnit(taskItems.getCZDW());
                           ticket.setDetail(detail1);
                           ticketList.add(ticket);
                       }
                       else {
                           Ticket ticket = new Ticket();
                           ticket.setMyorder(taskItems.getDINDEX());

                           ticket.setUnit(taskItems.getCZDW());
                           ticket.setDetail(taskItems.getCZNR().replaceAll(" ", ""));
                           ticketList.add(ticket);
                       }
                   }
                   for (int i=0;i<=matcher.groupCount();i++){
                       System.out.println(matcher.group(i));
                   }
               }
           }
           if (ticketType == 0){
               Matcher matcher = pattern_ZBJX.matcher(CZMD);
               if (matcher.find()){
                   ticketType = 2;
                   String unit = (matcher.group(1)+matcher.group(3)).replaceAll(" ", "");
                   String detail = "主变运行转检修";
                   Ticket ticket0 = new Ticket();
                   ticket0.setMyorder(0);
                   ticket0.setUnit(unit);
                   ticket0.setDetail(detail);
                   ticketList.add(ticket0);

                   for (int i=0;i<=matcher.groupCount();i++){
                       System.out.println(matcher.group(i));
                   }
               }
           }
           if (ticketType == 0){
               Matcher matcher = pattern_DMX.matcher(CZMD);
               if (matcher.find()){
                   ticketType = 3;
                   String detail = matcher.group(0).replaceAll(" ", "");
                   String unit = dispatchOperation.getTaskItemsArrayList().get(0).getCZDW();
                   Ticket ticket0 = new Ticket();
                   ticket0.setMyorder(0);
                   ticket0.setUnit(unit);
                   ticket0.setDetail(detail);
                   ticketList.add(ticket0);
                   for (int i=0;i<=matcher.groupCount();i++){
                       System.out.println(matcher.group(i));
                   }
               }
           }
           if (ticketType == 0){
               Matcher matcher = pattern_RD.matcher(CZMD);
               if (matcher.find()){
                   ticketType = 4;
                   String detail = matcher.group(0).replaceAll(" ", "");
                   String unit = dispatchOperation.getTaskItemsArrayList().get(0).getCZDW();
                   Ticket ticket0 = new Ticket();
                   ticket0.setMyorder(0);
                   ticket0.setUnit(unit);
                   ticket0.setDetail(detail);
                   ticketList.add(ticket0);
                   for (int i=0;i<=matcher.groupCount();i++){
                       System.out.println(matcher.group(i));
                   }
               }
           }
           if (ticketType == 0){
               Matcher matcher = pattern_LD.matcher(CZMD);
               if (matcher.find()){
                   ticketType = 5;
                   String detail = matcher.group(0).replaceAll(" ", "");
                   String unit = dispatchOperation.getTaskItemsArrayList().get(0).getCZDW();
                   Ticket ticket0 = new Ticket();
                   ticket0.setMyorder(0);
                   ticket0.setUnit(unit);
                   ticket0.setDetail(detail);
                   ticketList.add(ticket0);
                   for (int i=0;i<=matcher.groupCount();i++){
                       System.out.println(matcher.group(i));
                   }
               }
           }
           send2DB(ticketType,dispatchOperation.getTask().getCZMD().replaceAll(" ", ""),dispatchOperation.getGenTime(),ticketList);
       }

        int i = 0;
        i++;





    }
    public void send2DB(int flag, String taskcontent,String time, List<Ticket> ticketList){
        Connection conn = null;
        String sql = null;
        PreparedStatement pstmt = null;
        String tableName = null;
        if (flag ==1)
            tableName = "TICKET_XL";
        else if (flag ==2)
            tableName = "TICKET_ZBJX";
        else if (flag == 3)
            tableName = "TICKET_DMX";
        else if (flag == 4)
            tableName = "TICKET_RD";
        else if (flag == 5)
            tableName = "TICKET_LD";

            try {

                conn = DbConnectionPool.getInstance().getConnection();


                sql = "delete from TICKET_STATE";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                stmt.close();

                sql = "insert into TICKET_STATE(NPTIME,FLAG,CZMD) values (?,?,?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1,time);
                pstmt.setInt(2,flag);
                pstmt.setString(3,taskcontent);
                pstmt.executeUpdate();
                pstmt.close();
                if (tableName!=null) {
                    //清除历史数据
                    sql = "delete from "+tableName;
                     stmt = conn.createStatement();
                    stmt.execute(sql);
                    stmt.close();
                    for (Ticket ticket : ticketList) {
                        sql = "insert into "+ tableName+" (MYORDER,UNIT,DETAIL) values (?,?,?)";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, ticket.getMyorder());
                        pstmt.setString(2, ticket.getUnit());
                        pstmt.setString(3, ticket.getDetail());
                        pstmt.executeUpdate();
                        pstmt.close();
                    }
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

    public void writeDataToXML(String outputLocation, double timemills){
        try {
            EquipFaultHandler equipFaultHandler = new EquipFaultHandler();
            ArrayList<FaultRate> faultRateList = new ArrayList<FaultRate>();
            ArrayList<WeatherInfo> weatherInfoList = new ArrayList<WeatherInfo>();
            ArrayList<RealtimeFaultRate> realtimeFaultRateList = new ArrayList<RealtimeFaultRate>();
            ArrayList<EquipStateAssessResult>equipStateAssessResultList = new ArrayList<EquipStateAssessResult>();
            equipFaultHandler.GetFaultRates(faultRateList);
            equipFaultHandler.GetWeatherInfos(weatherInfoList);
            equipFaultHandler.GetEquipStateAssessResult(equipStateAssessResultList);
            equipFaultHandler.CalculateRealFaultRates(weatherInfoList,faultRateList,equipStateAssessResultList,realtimeFaultRateList);

            //获取数据库的天气、故障率、设备状态评估、重要用户数据
            DataHandler dataHandler = new DataHandler();
            ArrayList weather = weatherInfoList;
            ArrayList important = new ArrayList();
            ArrayList realtime = realtimeFaultRateList;
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


            //将数据对象转换为events

            ArrayList<OpRisk> Max2opRiskArrayList = getMax2forEachRiskType(oprisk);
            ArrayList<OpRisk> MaxopRiskArrayList = getMaxforEachRiskType(Max2opRiskArrayList);

            ArrayList<EventFactory.InternalEvent> weatherInfoEvents = putWeatherInfos2Events(weather);
            ArrayList<EventFactory.InternalEvent> consumerEvents = putImportantConsumer2Events(important);
            ArrayList<EventFactory.InternalEvent> equipStateEvents = putEquipStateAssess2Events(stateassess);
            ArrayList<EventFactory.InternalEvent> realtimeFaultEvents =putRealtimeFault2Events(realtime);
            ArrayList<EventFactory.InternalEvent> opRiskEvents = putOpRisk2Events(Max2opRiskArrayList);
            ArrayList<EventFactory.InternalEvent> opRiskLevelEvents = putOpRiskLevel2Events(MaxopRiskArrayList);

            ArrayList<EventFactory.InternalEvent> dispatchRiskTaskEvents = putTask2Events(dispatchrisktask);
            ArrayList<EventFactory.InternalEvent> dispatchRiskTaskTableEvents = putTaskTable2Events(tasktable);
            ArrayList<EventFactory.InternalEvent> dispatchRiskStepRiskTableEvents = putDispatchRiskStepRiskTable2Events(dispatchrisksteprisktable);
            ArrayList<EventFactory.InternalEvent> dispatchRiskStepRiskEvents = putDispatchRiskStepRisk2Events(dispatchrisksteprisk);
            ArrayList<EventFactory.InternalEvent> dispatchRiskRiskBarEvents = putDispatchRiskRiskBar2Events(dispatchriskriskbar);

            ArrayList<EventFactory.InternalEvent> dispatchCompTaskEvents = putTask2Events(dispatchcomptask);
            ArrayList<EventFactory.InternalEvent> dispatchCompTaskTable1Events = putTaskTable2Events(tasktable1);
            ArrayList<EventFactory.InternalEvent> dispatchCompTaskTable2Events = putTaskTable2Events(tasktable2);
            ArrayList<EventFactory.InternalEvent> dispatchCompStepRiskTableEvents = putDispatchCompStepRiskTable2Events(dispatchcompsteprisktable);
            ArrayList<EventFactory.InternalEvent> dispatchCompRiskCompBarEvents = putDispatchCompRiskCompBar2Events(dispatchcompriskcompbar);

            //将events写入XML中
            LinkedHashMap<String, ArrayList<EventFactory.InternalEvent>> name2EventListMap = new LinkedHashMap<String, ArrayList<EventFactory.InternalEvent>>();
            name2EventListMap.put("weatherInfoEvents",weatherInfoEvents);
            name2EventListMap.put("consumerEvents",consumerEvents);
            name2EventListMap.put("deviceStateEvents",equipStateEvents);
            name2EventListMap.put("equipFaultEvents",realtimeFaultEvents);
            name2EventListMap.put("opRiskEvents", opRiskEvents);
            name2EventListMap.put("opRiskLevelEvents", opRiskLevelEvents);

            name2EventListMap.put("dispatchRiskTaskEvents",dispatchRiskTaskEvents);
            name2EventListMap.put("dispatchRiskTaskTableEvents",dispatchRiskTaskTableEvents);
            name2EventListMap.put("dispatchRiskStepRiskTableEvents",dispatchRiskStepRiskTableEvents);
            name2EventListMap.put("dispatchRiskStepRiskEvents",dispatchRiskStepRiskEvents);
            name2EventListMap.put("dispatchRiskRiskBarEvents",dispatchRiskRiskBarEvents);

            name2EventListMap.put("dispatchCompTaskEvents",dispatchCompTaskEvents);
            name2EventListMap.put("dispatchCompTaskTable1Events",dispatchCompTaskTable1Events);
            name2EventListMap.put("dispatchCompTaskTable2Events",dispatchCompTaskTable2Events);
            name2EventListMap.put("dispatchCompStepRiskTableEvents",dispatchCompStepRiskTableEvents);
            name2EventListMap.put("dispatchCompRiskCompBarEvents",dispatchCompRiskCompBarEvents);



            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(timemills);
            String fileName = "SZ_" + time + "_RDS.xml";
            WriteXML xml = new WriteXML(outputLocation+fileName, "RDSResult", time);
            xml.WriteTheWhole(name2EventListMap);
            xml.end();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public ArrayList<EventFactory.InternalEvent> putWeatherInfos2Events( ArrayList<WeatherInfo> weatherInfoArrayList)  {
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (WeatherInfo weatherInfo:weatherInfoArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("IssueTime",weatherInfo.getIssueTime());
            internalEvent.putAttribute("SignalType",weatherInfo.getSignalType());
            internalEvent.putAttribute("SignalLevel",weatherInfo.getSignalLevel());
            internalEvent.putAttribute("District",weatherInfo.getDistrict());
            internalEvent.putAttribute("IssueState",weatherInfo.getIssueState());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putImportantConsumer2Events( ArrayList<ImportantConsumer> importantConsumerArrayList ){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (ImportantConsumer importantConsumer:importantConsumerArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Station",importantConsumer.getStationName());
            internalEvent.putAttribute("Consumer",importantConsumer.getConsumerName());
            internalEvent.putAttribute("LoadLevel",importantConsumer.getImportanceLevel());
            internalEvent.putAttribute("VoltLevel",String.valueOf(importantConsumer.getVoltageLevel()));
            internalEvent.putAttribute("Bureau",importantConsumer.getBureau());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putEquipStateAssess2Events(ArrayList<EquipStateAssessResult> equipStateAssessResultArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        for (EquipStateAssessResult equipStateAssessResult:equipStateAssessResultArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("AssessTime",sdf.format(equipStateAssessResult.getAssessTime()));
            internalEvent.putAttribute("EquName",equipStateAssessResult.getLineName());
            internalEvent.putAttribute("VoltLevel",String.valueOf(equipStateAssessResult.getVoltageLevel()));
            internalEvent.putAttribute("AssessState",equipStateAssessResult.getAssessResult());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putRealtimeFault2Events(ArrayList<RealtimeFaultRate> realtimeFaultRateArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (RealtimeFaultRate realtimeFaultRate:realtimeFaultRateArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("IssueTime",sdf.format(realtimeFaultRate.getIssueTime()));
            internalEvent.putAttribute("EquName",realtimeFaultRate.getLineName());
            internalEvent.putAttribute("EquType",realtimeFaultRate.getDeviceType());
            internalEvent.putAttribute("VoltLevel",String.valueOf(realtimeFaultRate.getVoltageLevel()));
            internalEvent.putAttribute("FaultRate",String.valueOf(realtimeFaultRate.getRealtimeFaultRate()));
            internalEvent.putAttribute("FaultLevel",realtimeFaultRate.getProbabilityLevel());
            internalEvent.putAttribute("FaultReason",realtimeFaultRate.getMainReason());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public  ArrayList<EventFactory.InternalEvent> putOpRisk2Events(ArrayList<OpRisk> opRiskArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        for (OpRisk opRisk:opRiskArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("AssessTime",sdf.format(opRisk.getAssessTime()));
            internalEvent.putAttribute("RiskType",opRisk.getRiskType());
            internalEvent.putAttribute("RiskDetail",opRisk.getRiskKind());
            String riskLevel = String.valueOf( 6-opRisk.getRiskLevel());
            if (opRisk.getRiskLevel()==0 || opRisk.getRiskLevel()==6)
                riskLevel = String.valueOf(0);
            internalEvent.putAttribute("RiskLevel",riskLevel);
            internalEvent.putAttribute("ProbLevel",String.valueOf( 6-opRisk.getReasonLevel()));
            internalEvent.putAttribute("SevLevel",String.valueOf(6-opRisk.getConseqLevel()));
            internalEvent.putAttribute("RiskReason",opRisk.getRiskReason());
            internalEvent.putAttribute("RiskConseq",opRisk.getRiskConseq());
            internalEvent.putAttribute("VitalLoad",opRisk.getImportantConsumer());
            internalEvent.putAttribute("IsMax",String.valueOf(opRisk.getIsMax()));
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public  ArrayList<EventFactory.InternalEvent> putOpRiskLevel2Events( ArrayList<OpRisk> opRiskArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
        for (OpRisk opRisk:opRiskArrayList){
            String riskLevel = String.valueOf( 6-opRisk.getRiskLevel());
            if (opRisk.getRiskLevel()==0 || opRisk.getRiskLevel()==6)
                riskLevel = String.valueOf(0);

            if (opRisk.getRiskType().equals("电网运行风险等级"))
                internalEvent.putAttribute("OpRiskLevel",riskLevel);
//            else if (opRisk.getRiskType().equals("低电压风险"))
//                internalEvent.putAttribute("LowVoltLevel",riskLevel);
//            else if (opRisk.getRiskType().equals("过电压风险"))
//                internalEvent.putAttribute("HighVoltLevel",riskLevel);
            else if (opRisk.getRiskType().equals("失负荷风险"))
                internalEvent.putAttribute("LoadLossLevel",riskLevel);
            else if (opRisk.getRiskType().equals("失出力风险"))
                internalEvent.putAttribute("OutputLossLevel",riskLevel);
            else if (opRisk.getRiskType().equals("设备过载风险"))
                internalEvent.putAttribute("OverloadLevel",riskLevel);
            else if (opRisk.getRiskType().equals("厂站全停风险"))
                internalEvent.putAttribute("StatLossLevel",riskLevel);
        }
        internalEvents.add(internalEvent);
        return internalEvents;
    }
    public ArrayList<OpRisk> getMax2forEachRiskType(ArrayList<OpRisk> opRiskArrayList){
        ArrayList<OpRisk> max2forEachRiskTypeList = new ArrayList<OpRisk>();

        Map<String,ArrayList<OpRisk>> riskType2RisksMap = new HashMap<String, ArrayList<OpRisk>>();
        riskType2RisksMap.put("失出力风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("失负荷风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("厂站全停风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("设备过载风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("过电压风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("低电压风险",new ArrayList<OpRisk>());

        for (OpRisk opRisk:opRiskArrayList){
            String risktype = opRisk.getRiskType();
            riskType2RisksMap.get(risktype).add(opRisk);
        }
        riskType2RisksMap.get("过电压风险").clear();
        riskType2RisksMap.get("低电压风险").clear();
        for (String risktype:riskType2RisksMap.keySet()){
            double max1 =0,max2=-1,max3=-2;
            OpRisk opRisk1 = null;
            OpRisk opRisk2 = null;
            OpRisk opRisk3 = null;
            for (OpRisk opRisk: riskType2RisksMap.get(risktype)){
                if (opRisk.getSeverity() > max1){
                    opRisk1 = opRisk;
                    max1 = opRisk.getSeverity();
                }
                else if (opRisk.getSeverity() > max2){
                    opRisk2 = opRisk;
                    max2 = opRisk.getSeverity();
                }
                else if (opRisk.getSeverity() > max3){
                    opRisk3 = opRisk;
                    max3 = opRisk.getSeverity();
                }
            }
            if ( opRisk1 != null)
                max2forEachRiskTypeList.add(opRisk1);
            if ( opRisk2 != null)
                max2forEachRiskTypeList.add(opRisk2);
            if ( opRisk3 != null)
                max2forEachRiskTypeList.add(opRisk3);
        }
        return max2forEachRiskTypeList;
    }
    public ArrayList<OpRisk> getMaxforEachRiskType (ArrayList<OpRisk> opRiskArrayList){
        ArrayList<OpRisk> maxforEachRiskTypeList = new ArrayList<OpRisk>();

        Map<String,ArrayList<OpRisk>> riskType2RisksMap = new HashMap<String, ArrayList<OpRisk>>();
        riskType2RisksMap.put("失出力风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("失负荷风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("厂站全停风险",new ArrayList<OpRisk>());
        riskType2RisksMap.put("设备过载风险",new ArrayList<OpRisk>());
//        riskType2RisksMap.put("过电压风险",new ArrayList<OpRisk>());
//        riskType2RisksMap.put("低电压风险",new ArrayList<OpRisk>());

        for (OpRisk opRisk:opRiskArrayList){
            String risktype = opRisk.getRiskType();
            riskType2RisksMap.get(risktype).add(opRisk);
        }
        for (String risktype:riskType2RisksMap.keySet()){
            double max =0;
            OpRisk opRisk1 = null;
            for (OpRisk opRisk: riskType2RisksMap.get(risktype)){
                if (opRisk.getSeverity() > max){
                    opRisk1 = opRisk;
                    max = opRisk.getSeverity();
                }
            }
            if ( opRisk1 != null)
                maxforEachRiskTypeList.add(opRisk1);
            if (opRisk1 == null){
                opRisk1 = new OpRisk();
                opRisk1.setRiskType(risktype);
                opRisk1.setRiskLevel( 0 );
                maxforEachRiskTypeList.add(opRisk1);
            }

        }
        OpRisk opRiskMaxAll = new OpRisk();
        for (OpRisk opRisk:maxforEachRiskTypeList){
            if (opRisk.getRiskLevel()>opRiskMaxAll.getRiskLevel()){
                opRiskMaxAll.setRiskLevel(opRisk.getRiskLevel());
                opRiskMaxAll.setRiskType("电网运行风险等级");
            }

        }
        for (OpRisk opRisk:opRiskArrayList){
            if (opRisk.getRiskLevel() == opRiskMaxAll.getRiskLevel())
                opRisk.setIsMax(1);
            else
                opRisk.setIsMax(0);
        }
        maxforEachRiskTypeList.add(opRiskMaxAll);
        for(OpRisk opRisk:maxforEachRiskTypeList){
            if (opRisk.getRiskLevel() == 0)
                opRisk.setRiskLevel(6);
        }
        return maxforEachRiskTypeList;
    }

    public ArrayList<EventFactory.InternalEvent> putTask2Events(ArrayList<Task> taskArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (Task task:taskArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Success",String.valueOf(task.getSuccess()));
            internalEvent.putAttribute("Task",task.getCZMD());
            internalEvent.putAttribute("TaskTime",sdf.format(task.getCZSJ()));
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putTaskTable2Events(ArrayList<TaskTable> taskTableArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (TaskTable taskTable:taskTableArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Order",String.valueOf(taskTable.getOrder()));
            internalEvent.putAttribute("Unit",taskTable.getUnit());
            internalEvent.putAttribute("Detail",taskTable.getDetail());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putDispatchRiskRiskBar2Events(ArrayList<DispatchRiskRiskBar> dispatchRiskRiskBars){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (DispatchRiskRiskBar dispatchRiskRiskBar:dispatchRiskRiskBars){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Order",String.valueOf(dispatchRiskRiskBar.getMyorder()));
            String riskLevel = String.valueOf( 6-dispatchRiskRiskBar.getBarlevel());
            if (dispatchRiskRiskBar.getBarlevel()==0 || dispatchRiskRiskBar.getBarlevel()==6)
                riskLevel = String.valueOf(0);
            internalEvent.putAttribute("BarLevel",riskLevel);
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putDispatchRiskStepRisk2Events(ArrayList<DispatchRiskStepRisk> dispatchRiskStepRiskArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (DispatchRiskStepRisk dispatchRiskStepRisk:dispatchRiskStepRiskArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Order",String.valueOf(dispatchRiskStepRisk.getMyorder()));
            internalEvent.putAttribute("StatLossLevel",String.valueOf( 6-dispatchRiskStepRisk.getStalosslevel()));
            internalEvent.putAttribute("LoadLossLevel",String.valueOf( 6-dispatchRiskStepRisk.getLoadlosslevel()));
            internalEvent.putAttribute("OutputLossLevel",String.valueOf( 6-dispatchRiskStepRisk.getOutputlosslevel()));
            internalEvent.putAttribute("LowVoltLevel",String.valueOf( 6-dispatchRiskStepRisk.getLowvoltlevel()));
//            internalEvent.putAttribute("OverloadLevel",String.valueOf( 6-dispatchRiskStepRisk.getOverloadlevel()));
//            internalEvent.putAttribute("HighVoltLevel",String.valueOf( 6-dispatchRiskStepRisk.getHighvoltlevel()));
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putDispatchRiskStepRiskTable2Events(ArrayList<DispatchRiskStepRiskTable> dispatchRiskStepRiskTableArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (DispatchRiskStepRiskTable dispatchRiskStepRiskTable:dispatchRiskStepRiskTableArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("Order",String.valueOf(dispatchRiskStepRiskTable.getMyorder()));
            internalEvent.putAttribute("RiskType",dispatchRiskStepRiskTable.getRisktype());

            String riskLevel = String.valueOf( 6-dispatchRiskStepRiskTable.getRisklevel());
            if (dispatchRiskStepRiskTable.getRisklevel()==0 || dispatchRiskStepRiskTable.getRisklevel()==6)
                riskLevel = String.valueOf(0);
            internalEvent.putAttribute("RiskLevel",riskLevel);

            internalEvent.putAttribute("ProbLevel",String.valueOf( 6-dispatchRiskStepRiskTable.getProblevel()));

            String conseqLevel = String.valueOf(dispatchRiskStepRiskTable.getSevlevel());
            if (dispatchRiskStepRiskTable.getSevlevel() > 5)
                conseqLevel = String.valueOf(5);
            internalEvent.putAttribute("SevLevel",conseqLevel);
            internalEvent.putAttribute("RiskReason",dispatchRiskStepRiskTable.getRiskreason());
            internalEvent.putAttribute("RiskEqu",dispatchRiskStepRiskTable.getRiskque());
            internalEvent.putAttribute("RiskSev",dispatchRiskStepRiskTable.getRisksev());
            internalEvent.putAttribute("VitalLoad",dispatchRiskStepRiskTable.getVitalload());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }

    public ArrayList<EventFactory.InternalEvent> putDispatchCompRiskCompBar2Events(ArrayList<DispatchCompRiskCompBar> dispatchCompRiskCompBarArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (DispatchCompRiskCompBar dispatchCompRiskCompBar:dispatchCompRiskCompBarArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("OpType",dispatchCompRiskCompBar.getOptype());

            String riskLevel = String.valueOf( 6-dispatchCompRiskCompBar.getBarlevel());
            if (dispatchCompRiskCompBar.getBarlevel()==0 || dispatchCompRiskCompBar.getBarlevel()==6)
                riskLevel = String.valueOf(0);
            internalEvent.putAttribute("BarLevel",riskLevel);
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }
    public ArrayList<EventFactory.InternalEvent> putDispatchCompStepRiskTable2Events(ArrayList<DispatchCompStepRiskTable> dispatchCompStepRiskTableArrayList){
        ArrayList<EventFactory.InternalEvent> internalEvents = new ArrayList<EventFactory.InternalEvent>();
        for (DispatchCompStepRiskTable dispatchCompStepRiskTable: dispatchCompStepRiskTableArrayList){
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("OpType",dispatchCompStepRiskTable.getOptype());
            internalEvent.putAttribute("Order",String.valueOf(dispatchCompStepRiskTable.getMyorder()));
            internalEvent.putAttribute("RiskType",dispatchCompStepRiskTable.getRisktype());

            String riskLevel = String.valueOf( 6-dispatchCompStepRiskTable.getRisklevel());
            if (dispatchCompStepRiskTable.getRisklevel()==0 || dispatchCompStepRiskTable.getRisklevel()==6)
                riskLevel = String.valueOf(0);

            internalEvent.putAttribute("RiskLevel",riskLevel);
            internalEvent.putAttribute("ProbLevel",String.valueOf( 6-dispatchCompStepRiskTable.getProblevel()));

            String conseqLevel = String.valueOf(dispatchCompStepRiskTable.getSevlevel());
            if (dispatchCompStepRiskTable.getSevlevel() > 5)
                conseqLevel = String.valueOf(5);
            internalEvent.putAttribute("SevLevel",conseqLevel);
            internalEvent.putAttribute("RiskReason",dispatchCompStepRiskTable.getRiskreason());
            internalEvent.putAttribute("RiskEqu",dispatchCompStepRiskTable.getRiskque());
            internalEvent.putAttribute("RiskSev",dispatchCompStepRiskTable.getRisksev());
            internalEvent.putAttribute("VitalLoad",dispatchCompStepRiskTable.getVitalload());
            internalEvents.add(internalEvent);
        }
        return internalEvents;
    }


}
