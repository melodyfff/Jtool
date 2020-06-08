package com.xinchen.tool.reflections.model;

import lombok.Data;

/**
 * @author xinchen
 * @version 1.0
 * @date 03/04/2020 16:43
 */
@Data
@AnnotationType("ok")
public class EntityUser implements EntityBase {
    private String name;

    public EntityUser(){
        name = "ok";
    }
}
