package com.xinchen.java.tools.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象复制工具类
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/1/1 20:36
 */
public class CopyUtils {

    public static Object copy(Object source){
        try {
            // ① 将当前对象写入流
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bot);
            oos.writeObject(source);

            // ② 从流中读取对象
            ByteArrayInputStream bin = new ByteArrayInputStream(bot.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
           return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
