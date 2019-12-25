package com.xinchen.tool.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * 采用java原生序列化方式与反序列方式,前提必须实现{@link Serializable}
 *
 * 对象转换为byte string
 *
 * byte string转换为对象
 *
 * @author xinchen
 * @version 1.0
 * @date 25/12/2019 10:28
 */
public class Object2ByteString {


    public static String toByteString(Object object){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(object);
            final byte[] bytes = arrayOutputStream.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(bytes[i]);
                if (i + 1 != bytes.length){
                    sb.append(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Object toObject(String objString){
        String[] arr = objString.split(",");
        byte[] bt = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            bt[i] = Byte.parseByte(arr[i]);
        }
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bt)).readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        final Demo demo = new Demo("Hello World!");

        String objString = toByteString(demo);
        System.out.println(objString);

        Object o = toObject(objString);
        System.out.println(o);

    }

    private static class Demo implements Serializable {
        private String name;

        public Demo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Demo{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
