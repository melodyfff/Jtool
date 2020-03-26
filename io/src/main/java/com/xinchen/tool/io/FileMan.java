package com.xinchen.tool.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 文件处理帮助类
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/1/30 15:01
 */
public class FileMan {

    /**
     * 拷贝文件
     * @param orgFilePath 原文件路径
     * @param desFilePath 目标文件路径
     */
    public static void copyFile(String orgFilePath,String desFilePath)  {
        long star = System.currentTimeMillis();
        try (FileInputStream in = new FileInputStream(new File(orgFilePath))){
            try (FileOutputStream out = new FileOutputStream(new File(desFilePath))){
                byte[] buff = new byte[in.available()];
                final int len = in.read(buff);
                System.out.println("size : "+len / 1024 +" kb");
                out.write(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Cost: "+ (System.currentTimeMillis()-star)+" ms");
        }
    }


    /**
     * 遍历文件路径，获取所有指定后缀的文件 ，如 .json .jpg .txt
     * @param filePath filePath文件路径
     * @param suffix 要搜寻的文件后缀
     * @return List<String> 指定后缀文件字符串集合
     */
    public static List<String> getFilePathStringsBySuffix(String filePath,String suffix) {
        File file = new File(filePath);
        List<String> pathList = new ArrayList<>();
        getFilePathStringsBySuffix(file, pathList,suffix);
        return pathList;
    }

    /**
     * 遍历文件路径，获取所有指定后缀的文件 ，如 .json .jpg .txt
     *
     * @param file File
     * @param pathList 存储搜寻到的路径
     * @param suffix 文件后缀
     */
    public static void getFilePathStringsBySuffix(File file, List<String> pathList,String suffix) {
        if (file.exists() && file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (file1.toString().endsWith(suffix)) {
                    pathList.add(file1.toString());
                    // System.out.println(file1);
                }
                getFilePathStringsBySuffix(file1,pathList,suffix);
            }
        }
    }

    /**
     * 根据指定文件路径所在目录搜寻指定同级目录下所有文件，获取其文件名-路径
     *
     * 如：
     * <pre>
     *     当前文件下存在 ： ./  hello.json  img/
     *     fromFilePathGetDirectoryFiles("./hello.json","img") 则会返回所有img目录下的文件
     *
     * </pre>
     *
     * 注: 搜寻的必须是一个同级文件夹
     *
     * @param filePath String 指定文件路径
     * @return Map<String,File> 文件名-路径
     */
    public static Map<String,File> fromFilePathGetDirectoryFiles(String filePath,String directoryName){
        final File file = new File(filePath);
        Map<String, File> imgNames = new HashMap<>();
        if (file.exists()){
            // 路径：  .../img  这里约定img文件夹为下载路径
            File imgFile = new File(file.getParentFile() + File.separator + directoryName);
            if (imgFile.isDirectory()){
                File[] files = imgFile.listFiles();
                if (null!= files){
                    for (File f:files) {
                        imgNames.put(f.getName(), f);
                    }
                }
            }
        }
        return imgNames;
    }

    /**
     * 获取文件字节流字符串，base64编码
     * @param file 文件路径
     * @return string
     * @throws IOException IOException
     */
    public static String readFileByteString(String file) throws IOException {
        try (final FileInputStream inStream = new FileInputStream(file)){
            byte[] buffer = new byte[inStream.available()];
            inStream.read(buffer);
            // 这里必须base64编码，不然转换为string的时候会多出一些
            return new String(Base64.getEncoder().encode(buffer), StandardCharsets.UTF_8);
        }
    }

    /**
     *  获取文件字节流字符串，base64编码
     * @param file File
     * @return string
     * @throws IOException IOException
     */
    public static String readFileByteString(File file) throws IOException {
        try (final FileInputStream in = new FileInputStream(file)){
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
                byte[] buffer = new byte[1024];
                int len;
                while (((len=in.read(buffer))!=-1)){
                    bos.write(buffer,0,len);
                }
                // 这里必须base64编码，不然转换为string的时候会多出一些
                return new String(Base64.getEncoder().encode(bos.toByteArray()), StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 将Base64加密过的字节字符串转存储为文件
     * @param byteString Base64加密过的字节字符串
     * @param file File
     * @throws IOException IOException
     */
    public static void byteString2File(String byteString,File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            try ( final ByteArrayOutputStream bot= new ByteArrayOutputStream()){
                bot.write(Base64.getDecoder().decode(byteString.getBytes()));
                bot.writeTo(fileOutputStream);
            }
        }
    }

    /**
     * 对字符串进行gzip压缩
     * @param s 等待压缩的字符串 string
     * @param inEncoding 字符串编码格式
     * @return 压缩后的字符串
     * @throws IOException IOException
     */
    public static String gzip(String s, Charset inEncoding) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(s.getBytes(inEncoding));
            // 这里一定要关闭，不然回报 java.io.EOFException: Unexpected end of ZLIB input stream
            gzip.close();
            // 编码ISO-8859-1不要进行修改，否则会导致java.util.zip.ZipException: Not in GZIP format异常
            return bos.toString(StandardCharsets.ISO_8859_1.name());
        }

    }

    /**
     * 对字符串进行gzip解压
     * @param s 压缩过的字符串 string
     * @param outEncoding 返回的解压后的字符串编码格式
     * @return 解压后的字符
     * @throws IOException IOException
     */
    public static String unGzip(String s, Charset outEncoding) throws IOException {
        // 编码ISO-8859-1不要进行修改，否则会导致java.util.zip.ZipException: Not in GZIP format异常
        try (ByteArrayInputStream bis = new ByteArrayInputStream(s.getBytes(StandardCharsets.ISO_8859_1.name()))) {
            try (GZIPInputStream gis = new GZIPInputStream(bis)) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = gis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    return bos.toString(outEncoding.name());
                }
            }
        }
    }

    public static void main(String[] args) {
//        System.out.println(getFilePathStringsBySuffix("",".json"));
        copyFile("C:\\Users\\xinchen\\Desktop\\1.5829631538520048E9.gif","C:\\Users\\xinchen\\Desktop\\hh.gif");
    }
}
