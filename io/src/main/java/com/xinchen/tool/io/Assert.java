package com.xinchen.tool.io;

/**
 * Fast-Fail
 * @author xinchen
 * @version 1.0
 * @date 28/10/2019 17:04
 */
public abstract class Assert {
    private Assert(){}

    public static void notNull(Object obj,String message){
        if (null == obj){
            throw new RuntimeException(message);
        }
    }
}
