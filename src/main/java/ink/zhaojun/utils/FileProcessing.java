package ink.zhaojun.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @description:
 * @author: zhaojun
 * @create: 2021-04-25 22:44
 */
@Slf4j
public class FileProcessing {

    public static String RULE_PROJECT_PATH = "C:\\Users\\hasee\\Desktop\\年度测试";

    public static String TARGET_PATH = "C:\\Users\\hasee\\Desktop\\输出";

    public static void main(String[] args) throws IOException {
        IOFileFilter fileFilter = new RegexFileFilter(".*.dta");
//        IOFileFilter fileFilter = FileFileFilter.FILE;
        Iterator<File> fileIterator = FileUtils.iterateFiles(new File(RULE_PROJECT_PATH),
                fileFilter, DirectoryFileFilter.DIRECTORY);
        while (fileIterator.hasNext()){
            File file = fileIterator.next();
//            System.out.println(file.getAbsolutePath());

            System.out.println(file.getParentFile().getName());
            // 先创建文件夹
//            File tartFile = new File(TARGET_PATH);
//
//            if(RULE_PROJECT_PATH.equals(TARGET_PATH)){
//                throw new IllegalArgumentException("项目目录与输出目录不能一样");
//            }
//
//            if (!tartFile.exists()) {
//                boolean mk = tartFile.mkdirs();
//            }


            //TODO 复制文件夹

        }
    }
}
