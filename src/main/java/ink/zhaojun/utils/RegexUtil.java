package ink.zhaojun.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static String getRegexStr(String source, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group() : null;
    }
}
