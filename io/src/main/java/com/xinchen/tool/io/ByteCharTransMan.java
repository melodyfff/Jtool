package com.xinchen.tool.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * 字符流字节流转换示例
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/23 21:59
 */
public class ByteCharTransMan {
    public static void main(String[] args) throws IOException {
        //
        String ok = "ok";

        // ByteArray
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ok.getBytes());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // InputStreamReader:   字节到字符的桥梁
        // OutputStreamWriter:  字符到字节的桥梁
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
        final InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);


        int len;
        while ((len=inputStreamReader.read())!=-1){
            outputStreamWriter.write(len);
            System.out.println((char)len);
        }

        // 从缓存区刷新
        outputStreamWriter.flush();
        System.out.println(byteArrayOutputStream.size());
        System.out.println(new String(byteArrayOutputStream.toByteArray()));
    }
}
