package ink.zhaojun.parsing;

import ink.zhaojun.domain.Contents;
import ink.zhaojun.domain.DataTableHead;
import ink.zhaojun.enums.SymbolEnum;
import ink.zhaojun.parsing.result.Result;
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
    public Result parsing(String path) throws DocumentException {
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File(path));
        log.info("获取文件：{}", path);

        Element root = document.getRootElement();

        Element definition = root.element("definition");

        //获取条件列定义
        Element conditionDefinitions = getForTagName("ConditionDefinitions", definition);
        log.info("获取条件列定义成功");

        // 条件列id集合
        List<String> conditionIdList = getAttrList("Id", conditionDefinitions);

        //获取结果列定义
        Element actionDefinitions = getForTagName("ActionDefinitions", definition);
        log.info("获取结果列定义成功");

        // 结果列id集合
        List<String> actionIdList = getAttrList("Id", actionDefinitions);

        // 合并id集合
        conditionIdList.addAll(actionIdList);

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

        // 临时存储index=0的内容
        Map<String, String> tempZeroMap = new HashMap<>();

        for (Element element : dataElements) {
            String attributeName = element.attribute("Name").getText();

            String definitionsId = RegexUtil.getRegexStr(attributeName, "[A-Z]\\d+");
            String type = RegexUtil.getRegexStr(attributeName, "\\[*\\d*\\]*\\#(\\w+)");
            String text = element.getText();

            if (type.contains("Height")) {
                continue;
            }

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
                        if (index == 0) {
                            tempZeroMap.put(definitionsId, text);
                        } else if (index == 1) {
                            headerTextList[1] = tempZeroMap.get(definitionsId);
                            headerTextList[index + 1] = text;
                        } else {
                            headerTextList[index + 1] = text;
                        }
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
        Result result = new Result();
        List<Map.Entry<String, DataTableHead>> sortHeadMap = sortHeadMap(dataTableHeadHashMap, conditionIdList);
        result.setDataTableHeadHashMap(sortHeadMap);
        result.setContentsList(contentsList);
        return result;
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
            Element textElement = expression.element("Text");
            List<String> paramList = new ArrayList<>();
            for (Element parEle : paramElementList) {
                paramList.add(textParsing(textElement).append(formattingStr(parEle.getText())).toString());
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
                        actionParamList.add(formattingStr(parEle.getText()));
                    }
                    action.setParamList(actionParamList);
                    contentsList.add(action);
                }
            }
        }
    }


    /**
     * 解析text元素
     * @param text
     * @return
     */
    public static StringBuilder textParsing(Element textElement){
        if(Objects.isNull(textElement)){
            return new StringBuilder(StringUtils.EMPTY);
        }
        String text = textElement.getText();
        if(SymbolEnum.LT.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.LT.getSymbol());
        }else if (SymbolEnum.ELT.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.ELT.getSymbol());
        }else if (SymbolEnum.GT.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.GT.getSymbol());
        }else if (SymbolEnum.EGT.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.EGT.getSymbol());
        }else if (SymbolEnum.EQ.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.EQ.getSymbol());
        }else if(SymbolEnum.NOT_IN.getChinese().equals(formattingStr(text))){
            return new StringBuilder(SymbolEnum.NOT_IN.getSymbol());
        }else {
            return new StringBuilder(text);
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

    /**
     * 获取指定的属性集合
     *
     * @param attr
     * @param element
     * @return
     */
    private List<String> getAttrList(String attr, Element element) {
        List<String> attrList = new ArrayList<>();
        if (Objects.isNull(element)) {
            return attrList;
        }
        List<Element> elements = element.elements();
        for (Element e : elements) {
            attrList.add(e.attribute(attr).getValue());
        }
        return attrList;
    }

    /**
     * 对表头进行排序
     *
     * @param dataTableHeadHashMap
     * @param orderList
     * @return
     */
    public List<Map.Entry<String, DataTableHead>> sortHeadMap(Map<String, DataTableHead> dataTableHeadHashMap, List<String> orderList) {
        List<Map.Entry<String, DataTableHead>> originalHeadList = new ArrayList<>(dataTableHeadHashMap.entrySet());
        List<Map.Entry<String, DataTableHead>> sortHeadList = new ArrayList<>();
        for (String orderId : orderList) {
            for (Map.Entry<String, DataTableHead> headEntry : originalHeadList) {
                if (orderId.equals(headEntry.getKey())) {
                    sortHeadList.add(headEntry);
                }
            }
        }
        return sortHeadList;
    }

    private static String formattingStr(String str) {
        if (StringUtils.isEmpty(str)) return str;
        return str.replace("\"", "").replace("{", "").replace("}", "");
    }
}
