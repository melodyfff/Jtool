package com.xinchen.java.tools.json;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * 测试对象
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2018/11/12 23:17
 */
@Data
public class TempObject implements Cloneable{
    @JSONField(name = "Id",ordinal = 1)
    private Long id;
    @JSONField(name = "Age",ordinal = 2)
    private int age;
    @JSONField(name = "Price",ordinal = 3)
    private double price;
    @JSONField(name = "Ok",ordinal = 4)
    private boolean ok;
    @JSONField(name = "Date",format = "yyyy-MM-dd HH:mm:ss",ordinal = 5)
    private Date date;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
