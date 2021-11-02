package com.zzn.pojie.zip;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
 
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
 
/**
 * @ClassName UnZipUtils
 * @Description
 * @Author yangxd
 * @Date 2021/5/7 10:20
 * @Version 1.0
 **/
public class UnZipUtils {
 
    public static void main(String[] args) throws IOException {
        String source = "D:\\ximo\\xxx.zip";
        String dest = "D:\\ximo\\xxx";
        String password = "1111";
        boolean unZip = unZip(source, dest, password);
        System.out.println(unZip);
    }
 
    /**
     * @param source   原始文件路径
     * @param dest     解压路径
     * @param password 解压文件密码(可以为空)
     */
    public static boolean unZip(String source, String dest, String password) {
        try {
            File zipFile = new File(source);
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset(StandardCharsets.UTF_8.name());
            // 解压目录
            File destDir = new File(dest);
            // 目标目录不存在时，创建该文件夹
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (zFile.isEncrypted()) {
                // 设置密码
                zFile.setPassword(password.toCharArray());
            }
            // 将文件抽出到解压目录(解压)
            zFile.extractAll(dest);
            List<FileHeader> headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            for (FileHeader fileHeader : headerList) {
                if (!fileHeader.isDirectory()) {
                    extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            for (File f : extractedFileList) {
                System.out.println(f.getAbsolutePath() + "文件解压成功!");
            }
        } catch (ZipException e) {
//            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}