package ink.zhaojun.excel;

import com.alibaba.excel.EasyExcel;
import ink.zhaojun.config.MyMergeStrategy;
import ink.zhaojun.domain.Contents;
import ink.zhaojun.domain.DataTableHead;
import ink.zhaojun.parsing.Parsing;
import ink.zhaojun.parsing.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelBuilder {

    private static final String SUFFIX = ".xlsx";

    private Map<String, Integer> headContentMapper = new LinkedHashMap<>();

    private List<Integer[]> mergeIndexList = new ArrayList<>();

    public void build(String sourceFilePath, String targetFilePath) throws DocumentException {
        Parsing parsing = new Parsing();
        Result result = parsing.parsing(sourceFilePath);

        List<Map.Entry<String, DataTableHead>> sortHeadList = result.getDataTableHeadHashMap();
        List<Contents> contentsList = result.getContentsList();

        String fileName = targetFilePath + SUFFIX;
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName)
                .head(head(sortHeadList))
                .registerWriteHandler(new MyMergeStrategy(mergeIndexList))
                .sheet()
                .doWrite(dataList(sortHeadList, contentsList));
    }

    /**
     * 表头生成
     *
     * @param sortHeadList
     * @return
     */
    public List<List<String>> head(List<Map.Entry<String, DataTableHead>> sortHeadList) {
        List<List<String>> headList = new ArrayList<>();
        headContentMapper = new LinkedHashMap<>();
        for (
                Map.Entry<String, DataTableHead> stringDataTableHeadEntry : sortHeadList) {
            DataTableHead dataTableHead = stringDataTableHeadEntry.getValue();
            String[] headerTextList = dataTableHead.getHeaderTextList();
            int count = 0;
            for (int i = 0; i < headerTextList.length; i++) {
                List<String> headSubList = new ArrayList<>();
                headSubList.add(headerTextList[0]);
                if (i != headerTextList.length - 1 && Objects.nonNull(headerTextList[i + 1])) {
                    count++;
                    headSubList.add(headerTextList[i + 1]);
                    headList.add(headSubList);
                    headContentMapper.put(stringDataTableHeadEntry.getKey(), count);
                } else {
                    if (i == 0) {
                        count++;
                        headList.add(headSubList);
                        headContentMapper.put(stringDataTableHeadEntry.getKey(), count);
                    }
                    break;
                }
            }
        }

        return headList;
    }

    /**
     * 填充空缺
     *
     * @param headContentMapper
     * @param lineContents
     * @return
     */
    private List<Contents> fillLineContents(Map<String, Integer> headContentMapper, List<Contents> lineContents, int lineIndex) {
        List<Contents> result = new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        headContentMapper.forEach((id, count) -> {
            if (lineIdContains(lineContents, id)) {
                Contents content = getById(id, lineContents);
                assert content != null;
                if (content.getParamList().isEmpty()) {
                    List<String> paramList = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        paramList.add(StringUtils.EMPTY);
                    }
                    content.setParamList(paramList);
                } else if (content.getParamList().size() != count) {
                    List<String> paramList = content.getParamList();
                    for (int i = 0; i < (count - content.getParamList().size()); i++) {
                        paramList.add(StringUtils.EMPTY);
                    }
                    int sumIndex = sumIndex(headContentMapper, content);
                    Integer[] mergeIndex = new Integer[]{lineIndex + 2, lineIndex + 2, sumIndex, sumIndex + count - 1};
                    mergeIndexList.add(mergeIndex);
                }
                result.add(content);
            } else {
                Contents content = new Contents();
                content.setDefId(id);
                List<String> paramList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    paramList.add(StringUtils.EMPTY);
                }
                content.setParamList(paramList);
                result.add(content);
            }
            index.getAndIncrement();
        });
        return result;
    }

    /**
     * 累加index 用于计算坐标
     *
     * @param headContentMapper
     * @param content
     * @return
     */
    private int sumIndex(Map<String, Integer> headContentMapper, Contents content) {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : headContentMapper.entrySet()) {
            String id = entry.getKey();
            Integer count = entry.getValue();
            if (content.getDefId().equals(id)) {
                return sum;
            }
            sum += count;
        }
        return 0;
    }


    /**
     * 通过id获取contents
     *
     * @param id
     * @param contentsList
     * @return
     */
    private Contents getById(String id, List<Contents> contentsList) {
        for (Contents contents : contentsList) {
            if (id.equals(contents.getDefId())) {
                return contents;
            }
        }
        return null;
    }

    private boolean lineIdContains(List<Contents> lineContents, String id) {
        Set<String> lineIdList = new HashSet<>();
        for (Contents lineContent : lineContents) {
            lineIdList.add(lineContent.getDefId());
        }
        return lineIdList.contains(id);
    }


    /**
     * 生成数据
     *
     * @param sortHeadList
     * @param contentsList
     * @return
     */
    private List<List<Object>> dataList(List<Map.Entry<String, DataTableHead>> sortHeadList, List<Contents> contentsList) {
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
        List<List<Object>> list = new ArrayList<>();
        int index = 0;
        for (List<Contents> lineContents : lineContentsList) {
            List<Object> data = new ArrayList<>();
            List<Contents> fillLineContents = fillLineContents(headContentMapper, lineContents, index);
            index++;
            for (Contents fillLineContent : fillLineContents) {
                data.addAll(fillLineContent.getParamList());
            }
            list.add(data);
        }
        return list;
    }
}
