package WriteXml;

import ReadXml.Constant;
import ReadXml.EventFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by CaoYu on 2015/11/20.
 */
public class WriteXML {
    SAXTransformerFactory fac = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

    private TransformerHandler handler = null;
    private OutputStream outStream = null;
    private String fileName;
    private AttributesImpl atts;
    private String rootElement;
    private String writeTime;
    //元素层次，用于控制XML缩进
    private static int level = 0;
    //每个层次父级缩进4个空格，即一个tab
    private static String tab = "    ";
    //系统换行符，Windows为："\n"，Linux/Unix为："/n"
    private static final String separator = System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1 ? "\n" : "/n";

    public WriteXML(String fileName, String rootElement,String time) {
        this.fileName = fileName;
        this.rootElement = rootElement;
        this.writeTime = time;
        init();
    }

    public void init() {
        try {
            handler = fac.newTransformerHandler();
            Transformer transformer = handler.getTransformer();
            //设置输出采用的编码方式
            transformer.setOutputProperty(OutputKeys.ENCODING, "gbk");
            //是否自动添加额外的空白
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //是否忽略xml声明
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            outStream = new FileOutputStream(fileName);
            Result resultxml = new StreamResult(outStream);
            handler.setResult(resultxml);
            atts = new AttributesImpl();
            start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            handler.startDocument();
            //设置schema和名称空间
            atts.addAttribute("", "", "Vendor", String.class.getName(), Constant.VENDER);
            atts.addAttribute("", "", "Receiver", String.class.getName(), Constant.RECEIVER);
            atts.addAttribute("", "", "Version", String.class.getName(), Constant.VERSION);
            atts.addAttribute("", "", "GenerateTime", String.class.getName(), writeTime);
            handler.startElement("", "", rootElement, atts);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //元素里面会嵌套子节点，因此元素的开始和结束分开写
    //如：<a><b>bcd</b></a>
    private void startElement(String objectElement, AttributesImpl attrs)
            throws SAXException {
        if(attrs == null){
            attrs = new AttributesImpl();
        }
        level++;
        appendTab();
        if (objectElement != null) {
            //注意，如果atts.addAttribute设置了属性，则会输出如：<a key="key" value="value">abc</a>格式
            //如果没有设置属性，则输出如：<a>abc</a>格式
            handler.startElement("", "", objectElement, attrs);
        }
    }

    //正常元素结束标记，如：</a>
    private void endElement(String objectElement) throws SAXException{

        appendTab();
        if (objectElement != null) {
            handler.endElement("", "", objectElement);
        }
        level--;
    }

    //自封闭的空元素，如<a key="key" value="value"/>，不用换行，写在一行时XML自动会自封闭
    private void endEmptyElement(String objectElement) throws SAXException{
        handler.endElement("", "", objectElement);
    }

    //无子节点的元素成为属性，如<a>abc</a>
    private void writeAttribute(String key, String value) throws SAXException{
        atts.clear();
        level++;
        appendTab();
        handler.startElement("", "", key, atts);
        handler.characters(value.toCharArray(), 0, value.length());
        handler.endElement("", "", key);
        level--;
    }

    public void end() {
        try {
            handler.endElement("", "", rootElement);
            // 文档结束,同步到磁盘
            handler.endDocument();
            outStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Tab缩进，SAX默认不自动缩进，因此需要手动根据元素层次进行缩进控制
    private void appendTab() throws SAXException{
        String indent = separator + "    ";
        for(int i = 1 ; i< level; i++){
            indent += tab;
        }
        handler.characters(indent.toCharArray(), 0, indent.length());
    }

    public void WriteEvent(EventFactory.InternalEvent event) throws SAXException{
        Map<String, String> props = event.getProps();
        Set<String> keys = props.keySet();

//        level = 0;
        //写<oes:Notification>节点
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "ID", String.class.getName(), "1");
        attributes.addAttribute("", "", "Name", String.class.getName(), "Jack");
        startElement(Constant.STUDENT, attributes);
        writeAttribute("ID",props.get("ID"));
        keys.remove("ID");
        writeAttribute("name",props.get("name"));
        keys.remove("name");
        writeAttribute("age",props.get("age"));
        keys.remove("age");

        attributes.clear();
        attributes.addAttribute("", "", "科目数", String.class.getName(), "2");
        startElement("各科成绩",attributes);
        for(String key:keys){
            writeAttribute(key, props.get(key));
        }
        endElement("各科成绩");
        //结束<oes:Notification>节点
        endElement(Constant.STUDENT);
    }
    public void WriteTheWhole(LinkedHashMap<String, ArrayList<EventFactory.InternalEvent>> name2EventListMap)throws SAXException{
        level = 0;
//==================================================================
        startElement("RiskInfo", null);
//-------------------------------------------------------------
        startElement("WeatherTable",null);
        ArrayList<EventFactory.InternalEvent> weatherInfoEvents = name2EventListMap.get("weatherInfoEvents");

            for (EventFactory.InternalEvent event:weatherInfoEvents){
                startElement("WeatherTableItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("WeatherTableItem");
            }

        endElement("WeatherTable");
//-------------------------------------------------------------
        startElement("ConsumerTable",null);
        ArrayList<EventFactory.InternalEvent> consumerEvents = name2EventListMap.get("consumerEvents");

            for (EventFactory.InternalEvent event:consumerEvents){
                startElement("ConsumerTableItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("ConsumerTableItem");
            }

        endElement("ConsumerTable");
//-------------------------------------------------------------
        startElement("EquStateTable",null);
        ArrayList<EventFactory.InternalEvent> deviceStateEvents = name2EventListMap.get("deviceStateEvents");

            for (EventFactory.InternalEvent event:deviceStateEvents){
                startElement("EquStateTableItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("EquStateTableItem");
            }

        endElement("EquStateTable");
//-------------------------------------------------------------
        endElement( "RiskInfo");
//==================================================================
        startElement("RiskIdentify", null);
//-------------------------------------------------------------
        startElement("EquRiskTable",null);
        ArrayList<EventFactory.InternalEvent> equipFaultEvents = name2EventListMap.get("equipFaultEvents");

            for (EventFactory.InternalEvent event:equipFaultEvents){
                startElement("EquRiskTableItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("EquRiskTableItem");
            }

        endElement("EquRiskTable");
//-------------------------------------------------------------
        endElement( "RiskIdentify");
//==================================================================
        if (name2EventListMap.get("dispatchRiskTaskEvents").size() > 0){
            AttributesImpl atts_risk = new AttributesImpl();
            atts_risk.addAttribute("", "", "Success", String.class.getName(), name2EventListMap.get("dispatchRiskTaskEvents").get(0).getProp("Success"));
            startElement("DispatchRiskAssessment", atts_risk);
//-------------------------------------------------------------
            startElement("Task",null);
            writeAttribute("Task",name2EventListMap.get("dispatchRiskTaskEvents").get(0).getProp("Task"));
            endElement( "Task");
//-------------------------------------------------------------
            startElement("TaskTime",null);
            writeAttribute("TaskTime",name2EventListMap.get("dispatchRiskTaskEvents").get(0).getProp("TaskTime"));
            endElement( "TaskTime");
//-------------------------------------------------------------
            startElement("TaskTable",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchRiskTaskTableEvents")){
                startElement("TaskTableItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("TaskTableItem");
            }
            endElement( "TaskTable");
//-------------------------------------------------------------
            startElement("RiskBar",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchRiskRiskBarEvents")){
                startElement("RiskBarItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("RiskBarItem");
            }
            endElement( "RiskBar");
//-------------------------------------------------------------
            startElement("StepRisk",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchRiskStepRiskEvents")){
                startElement("StepRiskItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("StepRiskItem");
            }
            endElement( "StepRisk");
//-------------------------------------------------------------
            startElement("StepRiskTable",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchRiskStepRiskTableEvents")){
                startElement("StepRiskTableItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("StepRiskTableItem");
            }
            endElement( "StepRiskTable");
            //-------------------------------------------------------------
            endElement( "DispatchRiskAssessment");
        }
//==================================================================
        if (name2EventListMap.get("dispatchCompTaskEvents").size() >0 ){
            AttributesImpl atts_comp = new AttributesImpl();
            atts_comp.addAttribute("", "", "Success", String.class.getName(), name2EventListMap.get("dispatchCompTaskEvents").get(0).getProp("Success"));
            startElement("DispatchRiskComp", atts_comp);
//-------------------------------------------------------------
            startElement("Task",null);
            writeAttribute("Task",name2EventListMap.get("dispatchCompTaskEvents").get(0).getProp("Task"));
            endElement( "Task");
//-------------------------------------------------------------
            startElement("TaskTime",null);
            writeAttribute("TaskTime",name2EventListMap.get("dispatchCompTaskEvents").get(0).getProp("TaskTime"));
            endElement( "TaskTime");
//-------------------------------------------------------------
            startElement("TaskTable1",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchCompTaskTable1Events")){
                startElement("TaskTableItem1",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("TaskTableItem1");
            }
            endElement( "TaskTable1");
//-------------------------------------------------------------
            startElement("TaskTable2",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchCompTaskTable2Events")){
                startElement("TaskTableItem2",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("TaskTableItem2");
            }
            endElement( "TaskTable2");
//-------------------------------------------------------------
            startElement("RiskCompBar",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchCompRiskCompBarEvents")){
                startElement("RiskCompBarItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("RiskCompBarItem");
            }
            endElement( "RiskCompBar");
//-------------------------------------------------------------
            startElement("StepRiskTable",null);
            for (EventFactory.InternalEvent internalEvent:name2EventListMap.get("dispatchCompStepRiskTableEvents")){
                startElement("StepRiskTableItem",null);
                Map<String, String> props = internalEvent.getProps();
                Set<String> keys = props.keySet();
                for (String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("StepRiskTableItem");
            }
            endElement( "StepRiskTable");
            //-------------------------------------------------------------
            endElement( "DispatchRiskComp");
        }
//==================================================================
        startElement("OpRiskAssessment", null);
//-------------------------------------------------------------
        startElement("OpRiskLevel",null);
        ArrayList<EventFactory.InternalEvent> opRiskLevelEvents = name2EventListMap.get("opRiskLevelEvents");

            for (EventFactory.InternalEvent event:opRiskLevelEvents){
                startElement("OpRiskItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("OpRiskItem");
            }


        endElement("OpRiskLevel");
//-------------------------------------------------------------
        startElement("OpRiskTable",null);
        ArrayList<EventFactory.InternalEvent> opRiskEvents = name2EventListMap.get("opRiskEvents");

            for (EventFactory.InternalEvent event:opRiskEvents){
                startElement("OpRiskTableItem",null);
                Map<String, String> props = event.getProps();
                Set<String> keys = props.keySet();
                for(String key:keys){
                    writeAttribute(key, props.get(key));
                }
                endElement("OpRiskTableItem");
            }

        endElement("OpRiskTable");

        endElement( "OpRiskAssessment");
//==================================================================
    }
}
