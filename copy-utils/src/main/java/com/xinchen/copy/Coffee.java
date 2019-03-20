package com.xinchen.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/1/1 16:01
 */
public class Coffee implements Serializable{

    private static final long serialVersionUID = 1L;

    private String coffeeName;

    private Origin origin;

    public Coffee(String coffeeName, String origin) {
        this.coffeeName = coffeeName;
        this.origin = new Origin(origin);
    }

    /**
     * 自定义序列号clone
     * @return new Coffee clone
     */
    public Coffee serialClone(){
        Coffee coffee = null;
        try {
            // ① 将当前对象写入流
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bot);
            oos.writeObject(this);

            // ② 从流中读取对象
            ByteArrayInputStream bin = new ByteArrayInputStream(bot.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            coffee = (Coffee) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return coffee;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }
}

class Origin implements Serializable{

    private static final long serialVersionUID = 1L;

    private String originName;

    public Origin(String originName) {
        this.originName = originName;
    }
    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }
}
