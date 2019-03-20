package com.xinchen.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * FastJson工具类
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2018/11/12 23:21
 */

public class FastJsonUtils {
    public static String object2Json(Object o) {
        return JSON.toJSONString(o);
    }

    public static Object json2Object(String json, Class<?> cls){
        return JSON.parseObject(json, cls);
    }

    public static <T> List<T> json2Array(String json, Class<T> cls){
        return JSON.parseArray(json, cls);
    }

    public static JSONArray jsonArray2JSONArray(String json){
        return JSONArray.parseArray(json);
    }

    public static <T> List<T> jsonArray2Array(String json,Class<T> cls){
        return JSONArray.parseArray(json,cls);
    }
}
