package ink.zhaojun.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Contents {
    private String defId;

    private List<String> paramList = new ArrayList<>();
}
