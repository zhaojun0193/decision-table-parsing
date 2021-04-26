package ink.zhaojun.utils;

import ink.zhaojun.domain.DataTableHead;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: zhaojun
 * @create: 2021-04-25 22:44
 */
public class FileUtil {
    public static void main(String[] args) throws DocumentException {
        SAXReader saxReader = new SAXReader();

        URL url = ClassLoader.getSystemClassLoader().getResource("test.dta");

        Document document = saxReader.read(url);
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();

        Element definition = root.element("definition");

        //获取条件列定义
        Element conditionDefinitions = getForTagName("ConditionDefinitions", definition);
        System.out.println(conditionDefinitions);

        //获取结果列定义
        Element actionDefinitions = getForTagName("ActionDefinitions", definition);
        System.out.println(actionDefinitions);

        //获取内容
        Element contents = getForTagName("Contents", definition);
        System.out.println(contents);

        //获取表头定义
        Element resourceSet = getForTagName("ResourceSet", definition);
        System.out.println(resourceSet);

        // 解析表头 --start
        Map<String, DataTableHead> dataTableHeadHashMap = new HashMap<>();
        List<Element> dataElements = resourceSet.elements();
        for (Element element : dataElements) {
            String attributeName = element.attribute("Name").getText();

            String definitionsId = RegexUtil.getRegexStr(attributeName, "[A-Z]\\d+");
            String type = RegexUtil.getRegexStr(attributeName, "\\[*\\d*\\]*\\#(\\w+)");
            String text = element.getText();

            if (!dataTableHeadHashMap.containsKey(definitionsId)) {
                DataTableHead dataTableHead = new DataTableHead();
                dataTableHead.setDefinitionsId(definitionsId);
                dataTableHeadHashMap.put(definitionsId, dataTableHead);
            }
            DataTableHead dataTableHead = dataTableHeadHashMap.get(definitionsId);
            if (StringUtils.isNotEmpty(type)) {
                if (type.contains("Width")) {
                    dataTableHead.setWidth(Integer.parseInt(text));
                } else if (type.contains("HeaderText")) {
                    String headIndex = RegexUtil.getRegexStr(type, "\\d");
                    String[] headerTextList = dataTableHead.getHeaderTextList();
                    if (StringUtils.isEmpty(headIndex)) {
                        headerTextList[0] = text;
                    } else {
                        int index = Integer.parseInt(headIndex);
                        headerTextList[index+1] = text;
                    }
                }
            }
        }
        // 解析表头 --end

        //解析内容
        assert contents != null;
        Element partition = (Element) contents.elements("Partition").get(0);
        List<Element> elements = partition.elements("Condition");
        for (Element element : elements) {
            Element expression = element.element("Expression");
            List<Element> paramList = expression.elements("Param");
            for (Element parEle : paramList) {
                System.out.println(parEle.getName() +"---" +parEle.getText());
            }
            Element partitionInner = element.element("Partition");
            //TODO
        }
    }

    public static Element getForTagName(String tagName, Element element) {
        List<Element> elements = element.elements();
        for (Element e : elements) {
            if (StringUtils.isNotEmpty(tagName) && tagName.equals(e.getName())) {
                return e;
            } else {
                Element forTagName = getForTagName(tagName, e);
                if (Objects.nonNull(forTagName)) {
                    return forTagName;
                }
            }
        }
        return null;
    }
}
