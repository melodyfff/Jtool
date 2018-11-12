package com.xinchen.java.tools.json;

import com.alibaba.fastjson.JSON;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2018/11/12 23:21
 */

public class JsonUtils {
    public static String object2JsonString(Object o) {
        return JSON.toJSONString(o);
    }

    public static Object jsonString2Object(String json, Class<?> cls){
        return JSON.parseObject(json, cls);
    }
}
