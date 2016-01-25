package FunctionClass;

import ReadXml.DispatchOperation;
import ReadXml.EventFactory;
import ReadXml.Task;
import ReadXml.TaskItems;
import WriteXml.WriteXML;
import WriteXml.XmlWriter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CaoYu on 2015/11/20.
 * 读XML时需要修改ReadXMLHandler里的ATTR_TAGS和Constant.STUDENT
 * 写XML时需要修改students，并将没一个对象转换成一个event。
 */
public class test {
    private static String inputFile = "f:/29.xml";
    private static String outputFile = "g:/output1.xml";
    private static List<EventFactory.InternalEvent> events;
    private  static XmlWriter xmlWriter;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            events = new EventFactory().read(inputFile);
            DispatchOperation dispatchOperation = new DispatchOperation();
            ArrayList<TaskItems> taskItemsArrayList = new ArrayList<TaskItems>();
            dispatchOperation.setTaskItemsArrayList(taskItemsArrayList);
            for (EventFactory.InternalEvent event:events){
                if (event.getProps().size() == 4){
                    dispatchOperation.setName(event.getProp("Name"));
                    dispatchOperation.setGenTime(event.getProp("GenTime"));
                    dispatchOperation.setReceiver(event.getProp("Receiver"));
                    dispatchOperation.setVender(event.getProp("Vender"));
                }
                if (event.getProps().size() == 8){
                    Task task = new Task();
                    task.setALLCZDW(event.getProp("ALLCZDW"));
                    task.setCZMD(event.getProp("CZMD"));
                    task.setCZPBH(event.getProp("CZPBH"));
                    task.setID(event.getProp("ID"));
                    task.setLX(event.getProp("LX"));
                    task.setNPR(event.getProp("NPR"));
                    task.setSFZCCZ(event.getProp("SFZCCZ"));
//                    task.setNPTTIME(Timestamp.valueOf(event.getProp("NPTTIME")));
                    dispatchOperation.setTask(task);
                }
                if (event.getProps().size() == 15){
                    TaskItems taskItems = new TaskItems();
                    taskItems.setID(event.getProp("ID"));
                    taskItems.setCZDW(event.getProp("CZDW"));
                    taskItems.setCZNR(event.getProp("CZNR"));
                    taskItems.setDINDEX(Integer.valueOf(event.getProp("DINDEX")));
                    taskItems.setEOID(Integer.valueOf(event.getProp("EOID")));
                    taskItems.setJSR(event.getProp("JSR"));
                    taskItems.setLSDX(event.getProp("LSDX"));
                    taskItems.setOINDEX(Integer.valueOf(event.getProp("OINDEX")));
                    taskItems.setSFQR(event.getProp("SFQR"));
                    taskItems.setSFWC(event.getProp("SFWC"));
                    taskItems.setXH(Integer.valueOf(event.getProp("XH")));
                    taskItems.setXLR(event.getProp("XLR"));
//                    taskItems.setXLSJ(Timestamp.valueOf(event.getProp("XLSJ")));
                    taskItems.setZXR(event.getProp("ZXR"));
//                    taskItems.setZXSJ(Timestamp.valueOf(event.getProp("ZXSJ")));
                    taskItemsArrayList.add(taskItems);
                }
            }
//            events = new EventFactory().read(inputFile);
            events = new ArrayList<EventFactory.InternalEvent>();
            EventFactory.InternalEvent internalEvent = new EventFactory.InternalEvent();
            internalEvent.putAttribute("ID","1");
            internalEvent.putAttribute("name","Jack");
            internalEvent.putAttribute("age","12");
            internalEvent.putAttribute("语文","90");
            internalEvent.putAttribute("数学","95");

            events.add(internalEvent);

            double timemills = System.currentTimeMillis();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_hhmmss");
            String time = sdf.format(timemills);
            WriteXML xml = new WriteXML(outputFile, "students",time);
            for(EventFactory.InternalEvent event :events ){
                xml.WriteEvent(event);
            }
            xml.end();


            int i=0;
            i++;
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms.");
    }
}
