import ink.zhaojun.domain.Contents;
import ink.zhaojun.domain.DataTableHead;
import ink.zhaojun.parsing.Parsing;
import ink.zhaojun.parsing.result.Result;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void filename() {
//        Pattern pattern = Pattern.compile("^\\w+.dta$");
////        Pattern pattern = Pattern.compile("黑龙江见费出单决策表.dta");
//
//        Matcher matcher = pattern.matcher("黑龙江见费出单决策表.dta");
//
//        System.out.println(matcher.matches());


        //创建指定匹配规则的模型
        Pattern pattern = Pattern.compile(".*.dta");
//创建匹配器
        Matcher matcher = pattern.matcher("黑龙江见费出单决策表.dta");

        if (matcher.matches()) {
            System.out.println(matcher.start() + " " + matcher.end() + " " + matcher.group());
        } else {
            System.out.println("没有匹配到！");

        }
    }

    @Test
    public void noModelWrite() throws DocumentException {
        // 写法1
        String fileName = "C:\\Users\\zxcl06\\Desktop\\输出\\" + "noModelWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
//        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
        head();
    }

    public List<List<String>> head() throws DocumentException {

        Parsing parsing = new Parsing();
        Result result = parsing.parsing("D:\\IDEAWorkSpace\\decision-table-parsing\\src\\main\\resources\\test.dta");

        List<Map.Entry<String, DataTableHead>> sortHeadList = result.getDataTableHeadHashMap();
        List<Contents> contentsList = result.getContentsList();


        List<List<String>> headList = new ArrayList<>();
        Map<String, Integer> headContentMapper = new LinkedHashMap<>();
        for (
                Map.Entry<String, DataTableHead> stringDataTableHeadEntry : sortHeadList) {
            DataTableHead dataTableHead = stringDataTableHeadEntry.getValue();
            String[] headerTextList = dataTableHead.getHeaderTextList();
            for (int i = 0; i < headerTextList.length; i++) {
                List<String> headSubList = new ArrayList<>();
                headSubList.add(headerTextList[0]);
                if (i != headerTextList.length - 1 && Objects.nonNull(headerTextList[i + 1])) {
                    headSubList.add(headerTextList[i + 1]);
                    headList.add(headSubList);
                    headContentMapper.put(stringDataTableHeadEntry.getKey(), headSubList.size());
                } else {
                    if (i == 0) {
                        headList.add(headSubList);
                        headContentMapper.put(stringDataTableHeadEntry.getKey(), headSubList.size());
                    }
                    break;
                }
            }
        }

        //先根据结果列拆分列表
        String lastActionId = sortHeadList.get(sortHeadList.size() - 1).getKey();
        List<List<Contents>> lineContentsList = new ArrayList<>();
        int formIndex = 0;
        for (int i = 0; i < contentsList.size(); i++) {
            if (contentsList.get(i).getDefId().equals(lastActionId)) {
                List<Contents> lineList = contentsList.subList(formIndex, ++i);
                lineContentsList.add(lineList);
                formIndex = i++;
            }
        }


        //列
        List<List<Object>> list = new ArrayList<>();
        for (List<Contents> lineContents : lineContentsList) {
            List<Object> data = new ArrayList<>();
            List<Contents> contentsList1 = fillLineContents(headContentMapper, lineContents);
            System.out.println(contentsList1);
        }


        return headList;
    }

    private List<Contents> fillLineContents(Map<String, Integer> headContentMapper, List<Contents> lineContents) {
        List<Contents> result = new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        headContentMapper.forEach((id, count) -> {
            if (lineIdContains(lineContents, id)) {
                Contents content = lineContents.get(index.get());
                result.add(content);
            } else {
                Contents content = new Contents();
                content.setDefId(id);
                content.setParamList(new ArrayList<>());
                result.add(content);
            }
            index.getAndIncrement();
        });
        return result;
    }

    private boolean lineIdContains(List<Contents> lineContents, String id) {
        Set<String> lineIdList = new HashSet<>();
        for (Contents lineContent : lineContents) {
            lineIdList.add(lineContent.getDefId());
        }
        return lineIdList.contains(id);
    }


    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (int i = 0; i < 10; i++) {
            List<Object> data = new ArrayList<Object>();
            data.add("字符串2" + i);
            data.add("字符串3" + i);
            data.add(new Date());
            data.add(0.56);
            list.add(data);
        }
        return list;
    }

    @Test
    public void str() {
        System.out.println(new String("C10").substring(0, 1));
    }

}
