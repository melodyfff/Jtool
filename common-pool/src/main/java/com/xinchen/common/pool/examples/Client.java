package com.xinchen.common.pool.examples;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * http://commons.apache.org/proper/commons-pool/examples.html
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/8/1 22:37
 */
public class Client {
    public static void main(String[] args) {
        ReaderUtil readerUtil = new ReaderUtil(new GenericObjectPool<StringBuffer>(new StringBufferFactory()));

    }
}
