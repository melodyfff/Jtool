package com.xinchen.common.pool.examples;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * http://commons.apache.org/proper/commons-pool/examples.html
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/8/1 22:34
 */
public class StringBufferFactory extends BasePooledObjectFactory<StringBuffer> {


    /**
     * 创建对象
     * @return StringBuffer
     * @throws Exception Exception
     */
    @Override
    public StringBuffer create() throws Exception {
        return new StringBuffer();
    }

    /**
     * 包裹对象实例
     * @param stringBuffer stringBuffer
     * @return PooledObject
     */
    @Override
    public PooledObject<StringBuffer> wrap(StringBuffer stringBuffer) {
        return new DefaultPooledObject<>(stringBuffer);
    }


    /**
     * 当对象返回池时，清除缓冲区
     * @param p p
     * @throws Exception Exception
     */
    @Override
    public void passivateObject(PooledObject<StringBuffer> p) throws Exception {
        p.getObject().setLength(0);
    }
}
