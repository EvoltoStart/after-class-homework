import org.annontion.MyScan;
import org.bean.Hello;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
@MyScan("org.bean.*")
public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException, IOException, SAXException {
        MyApplication application=new MyApplication("src/main/resources/application_aop.xml");
        Hello hello=(Hello) application.getBean("hello");
        hello.hello();
    }
}
