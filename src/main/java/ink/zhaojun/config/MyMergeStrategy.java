package ink.zhaojun.config;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MyMergeStrategy extends AbstractMergeStrategy {

    private List<Integer[]> mergeIndexList = new ArrayList<>();

    public MyMergeStrategy(List<Integer[]> mergeIndexList) {
        this.mergeIndexList = mergeIndexList;
    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();
        if (!isHead && inCellJudge(rowIndex,columnIndex)){
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cell.setCellStyle(cellStyle);
        }
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer integer) {
        List<String> headNameList = head.getHeadNameList();
        if (headNameList.size() == 1) {
            // size 为1 说明没有多列合并的情况
            return;
        }
        if (cell.getRowIndex() == 2 && cell.getColumnIndex() == 0) {
            for (Integer[] integers : mergeIndexList) {
                sheet.addMergedRegion(new CellRangeAddress(integers[0], integers[1], integers[2], integers[3]));
            }
        }
    }

    private boolean inCellJudge(int rowIndex,int columnIndex){
        for (Integer[] integers : this.mergeIndexList) {
            if(integers[0] == rowIndex){
                if(integers[2] <= columnIndex && columnIndex <= integers[3]){
                    return true;
                }
            }
        }
        return false;
    }
}
