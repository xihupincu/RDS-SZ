package ReadXml;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.*;

/**
 * Created by CaoYu on 2015/11/20.
 */
public class EventFactory {

    private XMLReader xmlReader;

    public static class InternalEvent {
//        private String notificationType = "";

        private Map<String, String> props = new LinkedHashMap<String, String>();
        private String elementName;

//        public String getNotificationType() {
//            return notificationType;
//        }


        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        public String getElementName() {
            return elementName;
        }

        public String getProp(String name) {
            String str = props.get(name);
            if (str == null) {
                return "";
            } else {
                return str;
            }
        }

        public Map<String, String> getProps(){
            return props;
        }

//        public void setNotificationType(String notificationType) {
//            this.notificationType = notificationType;
//        }

        public void putAttribute(String name, String value) {
            this.props.put(name, value);
        }

    }
    //调用SAX读取XML的方法，XML文件的数据会被存放到该List中
    public List<InternalEvent> read(String xmlPath) throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();

        xmlReader = saxParser.getXMLReader();
        List<InternalEvent> container = new LinkedList<InternalEvent>();
        ContentHandler handler =  new ReadXMLHandler(container);
        xmlReader.setContentHandler(handler);
        try {

            xmlReader.parse(new InputSource(xmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return container;
    }

}