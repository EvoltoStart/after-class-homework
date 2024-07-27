import org.annontion.Autowired;
import org.annontion.MyComponent;
import org.annontion.MyScan;
import org.bean.Hello;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MyApplication {

    private Map<String, Object> map=new HashMap<>();
    private String path;
    public MyApplication(String path) throws ParserConfigurationException, IOException, ClassNotFoundException, SAXException, InstantiationException, IllegalAccessException {

        this.path=path;
        parseXml(path);

        /**
         *第二种方式
         */
        //scanAnnotations(getScan());

    }
    private void parseXml(String path) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        File file=new File(path);
        String pa=getScan().replace("*","");
        DocumentBuilderFactory builderFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=builderFactory.newDocumentBuilder();
        Document document=builder.parse(file);
        NodeList list=document.getElementsByTagName("bean");
        for (int i = 0; i < list.getLength(); i++) {
            Element element= (Element) list.item(i);
            String id= element.getAttribute("id");
            String classname=element.getAttribute("class");
            Class<?> clz= Class.forName(pa+classname);
            Object o=clz.newInstance();
            map.put(id,o);
        }
    }
    public String getScan() throws ClassNotFoundException {
        Class clz=Class.forName("Main");
        MyScan myScan=(MyScan) clz.getAnnotation(MyScan.class);

        return myScan.value();

    }
    public void scanAnnotations(String path) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        File file = null;
        File[] files =null;
        Class clz=null;
        String id=null;
        String name=null;
        Object o=null;
        String ps=path.replace(".","/");
        String p=path.replace(".*","");
        if(ps.endsWith("*")) {
            ps=ps.replace("*","");
            file=new File("src/main/java/"+ps);
            files=file.listFiles();
        }else {
            file=new File(ps);
        }
        if(files!=null){
            for (File f:files){
                name= f.getName().replace(".java","");
                clz=Class.forName("org.bean."+name);
                MyComponent component= (MyComponent) clz.getAnnotation(MyComponent.class);
                id=component.value();

                 o=clz.newInstance();

                map.put(id,o);

            }
        }else {
            name= file.getName().replace(".java","");
            clz=Class.forName("org.bean."+name);
            MyComponent component=(MyComponent) clz.getAnnotation(MyComponent.class);
            id=component.value();
            o=clz.newInstance();
            map.put(id,o);
        }

        if (clz != null) {
            for(Field field:clz.getDeclaredFields()){
                if(field.isAnnotationPresent(Autowired.class)){
                    String fi=field.getType().getSimpleName().substring(0,1).toLowerCase()+field.getType().getSimpleName().substring(1).toUpperCase();
                    field.set(o,map.get(fi));
                }
            }
        }
    }

    public Object getBean(String id){
        return map.get(id);
    }
}
