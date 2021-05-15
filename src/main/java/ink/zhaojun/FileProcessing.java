package ink.zhaojun;

import ink.zhaojun.excel.ExcelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @description:
 * @author: zhaojun
 * @create: 2021-04-25 22:44
 */
@Slf4j
public class FileProcessing {

    public static String RULE_PROJECT_PATH = "D:\\workspace\\groupamaauto-rule-new";

    public static String TARGET_PATH = "C:\\Users\\zxcl06\\Desktop\\输出";

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();

        if (RULE_PROJECT_PATH.equals(TARGET_PATH)) {
            throw new IllegalArgumentException("项目目录与输出目录不能一样");
        }

        File project = new File(RULE_PROJECT_PATH);
        File target = new File(RULE_PROJECT_PATH);
        if (project.isFile() || target.isFile()) {
            throw new IllegalArgumentException("地址不能是文件");
        }

        IOFileFilter fileFilter = new RegexFileFilter(".*.dta");
//        IOFileFilter fileFilter = FileFileFilter.FILE;
        Iterator<File> fileIterator = FileUtils.iterateFiles(new File(RULE_PROJECT_PATH),
                fileFilter, DirectoryFileFilter.DIRECTORY);

        while (fileIterator.hasNext()) {
            File file = fileIterator.next();

            File targetFile = new File(file.getAbsolutePath().replace(project.getParent(), TARGET_PATH));
            if (!targetFile.getParentFile().exists()) {
                boolean mkdirs = targetFile.getParentFile().mkdirs();
            }
            ExcelBuilder excelBuilder = new ExcelBuilder();
            String fileName = file.getName();
            try {
                log.info("获取文件：{}", file.getAbsolutePath());
                excelBuilder.build(file.getAbsolutePath(), targetFile.getParent() + "\\" + fileName.split("\\.")[0]);
            } catch (Exception e) {
//                log.error("失败:{}",e.getMessage());
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        log.info("所有文件解析完毕，耗时{}ms",end - start);
    }
}
