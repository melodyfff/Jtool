package com.xinchen.tool.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/23 11:57
 */
public class ok {
    public static void main(String[] args) throws IOException {
        String say = "ok";
        final StringReader stringReader = new StringReader(say);

        final StringWriter stringWriter = new StringWriter();
        int l;
        while ((l=stringReader.read())!=-1){
            stringWriter.write(l);
            System.out.println((char) l);
        }

        System.out.println(stringWriter.getBuffer().toString());
    }
}
