import org.junit.Test;

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
}
