package com.xinchen.tool.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * 文件处理帮助类
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/1/30 15:01
 */
public class FileMan {

    /**
     * 遍历文件路径，获取所有json文件
     * @param filePath filePath文件路径
     * @return List<String> json文件集合
     */
    public static List<String> getJsonFilePath(String filePath) {
        File file = new File(filePath);
        List<String> pathList = new ArrayList<>();
        getJsonFilePath(file, pathList);
        return pathList;
    }

    /**
     * 遍历文件路径，获取所有json文件
     * @param file file
     * @param pathList 返回查询到的文件列表
     */
    public static void getJsonFilePath(File file, List<String> pathList) {
        if (file.exists() && file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (file1.toString().endsWith(".json")) {
                    pathList.add(file1.toString());
                    // System.out.println(file1);
                }
                getJsonFilePath(file1,pathList);
            }
        }
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
}
