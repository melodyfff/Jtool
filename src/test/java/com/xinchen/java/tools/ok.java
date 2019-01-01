package com.xinchen.java.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinchen.java.tools.json.TempObject;

import java.io.IOException;

/**
 * @author xinchen
 * @version 1.0
 * @date 11/12/2018 09:04
 */
public class ok {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


        TempObject tempObject = new TempObject();

        final TempObject tempObject1 = mapper.readValue("{\"id\":\"5\"}", TempObject.class);
        System.out.println(tempObject1);




    }
}
