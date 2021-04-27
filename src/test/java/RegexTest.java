import com.alibaba.excel.EasyExcel;
import ink.zhaojun.domain.DataTableHead;
import ink.zhaojun.parsing.Parsing;
import ink.zhaojun.parsing.result.Result;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        String fileName = "C:\\Users\\hasee\\Desktop\\输出\\" + "noModelWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
    }

    private List<List<String>> head() throws DocumentException {

        Parsing parsing = new Parsing();
        Result result = parsing.parsing("E:\\workspace\\idea\\decision-table-parsing\\src\\main\\resources\\test.dta");
        Map<String, DataTableHead> dataTableHeadHashMap = result.getDataTableHeadHashMap();
        // TODO

        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("字符串" + System.currentTimeMillis());
        List<String> head1 = new ArrayList<String>();
        head1.add("数字" + System.currentTimeMillis());
        List<String> head2 = new ArrayList<String>();
        head2.add("日期" + System.currentTimeMillis());
        list.add(head0);
        list.add(head1);
        list.add(head2);
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (int i = 0; i < 10; i++) {
            List<Object> data = new ArrayList<Object>();
            data.add("字符串" + i);
            data.add(new Date());
            data.add(0.56);
            list.add(data);
        }
        return list;
    }
}
