package ink.zhaojun.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SymbolEnum {

    LT("<一个 数字>小于<一个 数字>", "<"),
    GT("<一个 数字>大于<一个 数字>", ">"),
    ELT("<一个 数字>小于等于<一个 数字>", "≤"),
    EGT("<一个 数字>大于等于<一个 数字>", "≥"),
    EQ("<一个 数字>等于<一个 数字>", "="),
    NOT_IN("<一个 对象>不是<一些 对象>中的一个","!in");

    private String chinese;

    private String symbol;
}
