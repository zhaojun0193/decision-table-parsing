package ink.zhaojun.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataTableHead {
    private Integer width;

    private String[] headerTextList = new String[6];

    private String definitionsId;
}
