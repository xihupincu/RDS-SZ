package ReadXml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by CaoYu on 2015/11/20.
 */
public class ReadXMLHandler extends DefaultHandler {
    private List<EventFactory.InternalEvent> eventContainer;
    private StringBuilder buf = new StringBuilder();
    private EventFactory.InternalEvent event;
    private static final Set<String> ATTR_TAGS1 = new HashSet<String>();
    private static final Set<String> ATTR_TAGS2 = new HashSet<String>();

    static {
        //设置要读取的属性。
//        ATTR_TAGS.add(Constant.EVENT_TIME);
//        ATTR_TAGS.add(Constant.SPECIFIC_PROBLEM);
//        ATTR_TAGS.add(Constant.ALRAM_TEXT);
//        ATTR_TAGS.add(Constant.PERCEIVED_SEVERITY);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT1);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT2);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT3);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT4);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT5);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT6);
//        ATTR_TAGS.add(Constant.ADDITION_TEXT7);
//        ATTR_TAGS.add(Constant.EVENT_TYPE);
//        ATTR_TAGS.add(Constant.TIME_STAMP);

//        ATTR_TAGS.add(Constant.CLASSES);
//        ATTR_TAGS.add(Constant.AGE);
//        ATTR_TAGS.add(Constant.ID);
//        ATTR_TAGS.add(Constant.NAME);
        ATTR_TAGS1.add("ID");
        ATTR_TAGS1.add("NPR");
        ATTR_TAGS1.add("NPTIME");
        ATTR_TAGS1.add("CZMD");
        ATTR_TAGS1.add("ALLCZDW");
        ATTR_TAGS1.add("LX");
        ATTR_TAGS1.add("CZPBH");
        ATTR_TAGS1.add("SFZCCZ");

        ATTR_TAGS2.add("ID");
        ATTR_TAGS2.add("LSDX");
        ATTR_TAGS2.add("DINDEX");
        ATTR_TAGS2.add("CZDW");
        ATTR_TAGS2.add("CZNR");
        ATTR_TAGS2.add("SFQR");
        ATTR_TAGS2.add("OINDEX");
        ATTR_TAGS2.add("EOID");
        ATTR_TAGS2.add("JSR");
        ATTR_TAGS2.add("XLSJ");
        ATTR_TAGS2.add("XLR");
        ATTR_TAGS2.add("XH");
        ATTR_TAGS2.add("SFWC");
        ATTR_TAGS2.add("ZXR");
        ATTR_TAGS2.add("ZXSJ");
    }

    public ReadXMLHandler(List<EventFactory.InternalEvent> eventContainer) {
        this.eventContainer = eventContainer;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        buf.setLength(0);
        if (qName.trim().equals("DispatchOperation")){
            event = new EventFactory.InternalEvent();
            event.setElementName("DispatchOperation");
            eventContainer.add(event);
            String Name = attributes.getValue("Name");
            event.putAttribute("Name",Name);
            String Vender = attributes.getValue("Vender");
            event.putAttribute("Vender", Vender);
            String Receiver = attributes.getValue("Receiver");
            event.putAttribute("Receiver", Receiver);
            String GenTime = attributes.getValue("GenTime");
            event.putAttribute("GenTime", GenTime);
        }
        else if (qName.trim().equals("Task")) {
            event = new EventFactory.InternalEvent();
            event.setElementName("Task");
            eventContainer.add(event);
        }
        else if (qName.trim().equals("TaskItems")){
            event = new EventFactory.InternalEvent();
            event.setElementName("TaskItems");
            eventContainer.add(event);
        }
        //如果需要读一些属性值，如<a key="key" value="value"/>，就采用下面的方法。其中，a=Constant.MAP_ITEM，key=Constant.KEY
//        else if(qName.equals(Constant.MAP_ITEM)){
//            //获取元素中的属性值，如<a key="key" value="value"/>，获取key和value
//            String key = attributes.getValue(Constant.KEY);
//            event.putAttribute(Constant.KEY, key);
//            String value = attributes.getValue(Constant.VALUE);
//            event.putAttribute(Constant.VALUE, value);
//        }
//        else if(qName.equals(Constant.ALARM_NEW)){
//            String systemDn = attributes.getValue(Constant.SYSTEM_DN);
//            event.putAttribute(Constant.SYSTEM_DN, systemDn);
//        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (ATTR_TAGS1.contains(qName)){
            event.putAttribute(qName, buf.toString());
        }
       else if (ATTR_TAGS2.contains(qName)){
           event.putAttribute(qName, buf.toString());
       }
    }

    //获取元素值，如<a>abc</a>，获取其中的abc
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        buf.append(ch, start, length);
    }
}
