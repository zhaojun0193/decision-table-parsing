package ink.zhaojun.parsing.result;

import ink.zhaojun.domain.Contents;
import ink.zhaojun.domain.DataTableHead;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 解析结果封装
 * @author: zhaojun
 * @create: 2021-04-27 23:34
 */
@Data
public class Result {
    /**
     * 表头
     */
    private List<Map.Entry<String, DataTableHead>> dataTableHeadHashMap;

    /**
     * 内容
     */
    private List<Contents> contentsList;
}
