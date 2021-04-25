package ink.zhaojun.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 * @description:
 * @author: zhaojun
 * @create: 2021-04-25 22:44
 */
public class FileUtil {
    public static void main(String[] args) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        File file = new File("E:\\workspace\\idea\\decision-table-parsing\\xml\\test.dta");

        Document document = saxReader.read(file);
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();

        for (Element childElement : childElements) {
            if("definition".equals(childElement.getName())){
                List<Element> elements = childElement.elements();
                for (Element element : elements) {
                    List<Element> elements1 = element.elements();
                    for (Element element1 : elements1) {
                        List<Element> elements2 = element1.elements();
                        for (Element element2 : elements2) {
                            System.out.println(element2.getName());
                        }
                    }
                }
            }
        }
    }
}
