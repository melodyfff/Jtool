package com.xinchen.java.tools.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2018/11/12 23:22
 */
public class JsonUtilsTest {

    private TempObject tempObject;

    private List<Object> list;

    private String jsonList = "[{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"},{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:59:59\"}]";

    @Before
    public void init() throws CloneNotSupportedException {
        tempObject = new TempObject();
        tempObject.setId(1L);
        tempObject.setAge(24);
        tempObject.setPrice(23.0);
        tempObject.setOk(true);
        tempObject.setDate(new Date());

        list = Lists.newArrayList();

        for (int i = 0;i<10;i++){
            list.add( tempObject.clone());
        }

    }

    @Test
    public void object2JsonString() {
        System.out.println(JsonUtils.object2Json(tempObject));

        System.out.println(JsonUtils.object2Json(list));
    }

    @Test
    public void json2Object(){
        String json = "{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:54:08\"}";
        System.out.println(JsonUtils.json2Object(json,TempObject.class));


        final TempObject o = (TempObject) JsonUtils.json2Object(json, TempObject.class);
        System.out.println(o);
    }

    @Test
    public void jsonList2List(){
        System.out.println(JsonUtils.json2Array(jsonList,TempObject.class));

        final List<TempObject> o = JsonUtils.json2Array(jsonList, TempObject.class);
        System.out.println(o);

    }

    @Test
    public void jsonArray2JSONArray(){
        System.out.println(JsonUtils.jsonArray2JSONArray(jsonList));

        final JSONArray o =  JsonUtils.jsonArray2JSONArray(jsonList);
        o.forEach(System.out::println);

        final List<TempObject> tempObjects = o.toJavaList(TempObject.class);
        System.out.println(tempObjects);

    }
}