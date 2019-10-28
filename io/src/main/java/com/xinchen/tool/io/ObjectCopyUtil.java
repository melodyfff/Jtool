package com.xinchen.tool.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 功能: 对象复制,深拷贝
 *
 * @author xinchen
 * @version 1.0
 * @date 28/10/2019 17:03
 */
public final class ObjectCopyUtil {

    private ObjectCopyUtil(){}

    public static Object copy(Object source) throws IOException, ClassNotFoundException {
        Assert.notNull(source,"source object can't be null.");

        // 存储对象流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // ① 将当前对象写入流
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(source);

        // ② 从流中读取对象  non-transient and non-static fields
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        return ois.readObject();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Integer> source = new ArrayList<>();
        Object copy = copy(source);

        // true,由于没有重写hash()和equals()
        System.out.println(copy.equals(source));


        // source: [1]  copy: [] ,这里看出操作的是不同的对象
        source.add(1);
        System.out.println(source);
        System.out.println(copy);
        // false
        System.out.println(copy.equals(source));
    }
}
