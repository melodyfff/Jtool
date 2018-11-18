package com.xinchen.java.tools.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2018/11/12 23:22
 */
public class FastJsonUtilsTest {

    static final Logger LOG = LoggerFactory.getLogger(FastJsonUtilsTest.class);

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
        LOG.info(FastJsonUtils.object2Json(tempObject));

        LOG.debug(FastJsonUtils.object2Json(list));
    }

    @Test
    public void json2Object(){
        String json = "{\"Id\":1,\"Age\":24,\"Price\":23.0,\"Ok\":true,\"Date\":\"2018-11-12 23:54:08\"}";
        LOG.info(FastJsonUtils.json2Object(json,TempObject.class).toString());


        final TempObject o = (TempObject) FastJsonUtils.json2Object(json, TempObject.class);
        LOG.info(o.toString());
    }

    @Test
    public void jsonList2List(){
        LOG.info(FastJsonUtils.json2Array(jsonList,TempObject.class).toString());

        final List<TempObject> o = FastJsonUtils.json2Array(jsonList, TempObject.class);
        LOG.info(o.toString());

    }

    @Test
    public void jsonArray2JSONArray(){
        LOG.info(FastJsonUtils.jsonArray2JSONArray(jsonList).toJSONString());

        final JSONArray o =  FastJsonUtils.jsonArray2JSONArray(jsonList);
        o.forEach(System.out::println);
        Map map = (Map) o.get(0);
        System.out.println(map.get("Id"));

        final List<TempObject> tempObjects = o.toJavaList(TempObject.class);
        LOG.info(tempObjects.toString());
    }
    @Test
    public void jsonArray2Array(){
        final List<TempObject> tempObjects = FastJsonUtils.jsonArray2Array(jsonList, TempObject.class);
        LOG.info(tempObjects.toString());
    }
}