package ink.zhaojun.parsing;

import ink.zhaojun.domain.Contents;
import ink.zhaojun.domain.DataTableHead;
import ink.zhaojun.utils.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

@Slf4j
public class Parsing {

    @SuppressWarnings("unchecked")
    public void parsing(String path) throws DocumentException {
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File(path));
        log.info("获取文件：{}", path);

        Element root = document.getRootElement();

        Element definition = root.element("definition");

        //获取条件列定义
        Element conditionDefinitions = getForTagName("ConditionDefinitions", definition);
        log.info("获取条件列定义成功");

        //获取结果列定义
        Element actionDefinitions = getForTagName("ActionDefinitions", definition);
        log.info("获取结果列定义成功");

        //获取内容
        Element contents = getForTagName("Contents", definition);
        log.info("获取内容成功");

        //获取表头定义
        Element resourceSet = getForTagName("ResourceSet", definition);
        log.info("获取表头定义成功");

        log.info("开始解析表头");
        // 解析表头 --start
        Map<String, DataTableHead> dataTableHeadHashMap = new HashMap<>();
        assert resourceSet != null;
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
                        headerTextList[index + 1] = text;
                    }
                }
            }
        }
        log.info("解析表头结束");
        // 解析表头 --end

        // 解析内容
        List<Contents> contentsList = new ArrayList<>();
        log.info("开始解析内容");
        contentParsing(contents, contentsList);
        log.info("解析内容结束");

        log.info("开始生成文件");
    }

    /**
     * 内容解析
     *
     * @param contentsElement
     */
    @SuppressWarnings("unchecked")
    public static void contentParsing(Element contentsElement, List<Contents> contentsList) {
        if (Objects.isNull(contentsElement)) {
            return;
        }

        List<Element> partitionList = contentsElement.elements("Partition");
        if (partitionList.size() == 0) {
            return;
        }

        Element partition = partitionList.get(0);
        List<Element> ConditionList = partition.elements("Condition");


        for (Element element : ConditionList) {
            Contents contents = new Contents();
            String defId = partition.attribute("DefId").getValue();
            contents.setDefId(defId);
            Element expression = element.element("Expression");
            Element actionSet = element.element("ActionSet");

            //条件列
            List<Element> paramElementList = expression.elements("Param");
            if (paramElementList.isEmpty()) return;
            List<String> paramList = new ArrayList<>();
            for (Element parEle : paramElementList) {
                paramList.add(parEle.getText());
            }
            contents.setParamList(paramList);
            contentsList.add(contents);
            contentParsing(element, contentsList);

            //结果列
            if (Objects.nonNull(actionSet)) {
                List<Element> actionList = actionSet.elements("Action");
                for (Element actionElement : actionList) {
                    Contents action = new Contents();
                    action.setDefId(actionElement.attribute("DefId").getValue());
                    Element actionExpression = actionElement.element("Expression");
                    List<Element> actionParamElementList = actionExpression.elements("Param");
                    List<String> actionParamList = new ArrayList<>();
                    for (Element parEle : actionParamElementList) {
                        actionParamList.add(parEle.getText());
                    }
                    action.setParamList(actionParamList);
                    contentsList.add(action);
                }
            }
        }
    }

    /**
     * 通过标签名获取元素
     *
     * @param tagName
     * @param element
     * @return
     */
    @SuppressWarnings("unchecked")
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