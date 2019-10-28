package com.xinchen.tool.pool.example;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 *
 * 功能: 将{@link java.io.Reader}中的内容转换为String
 * 其中{@link StringBuffer}采用池化
 *
 * @author xinchen
 * @version 1.0
 * @date 28/10/2019 11:27
 */
public class ReaderUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReaderUtil.class);

    private ObjectPool<StringBuffer> pool;

    private ReaderUtil(){
        // 池配置
        GenericObjectPoolConfig<StringBuffer> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(0);
        pool = new GenericObjectPool<>(new StringBufferFactory(),poolConfig);
    }

    /**
     * 将{@link java.io.Reader}中的内容转换为String,转换完毕后关闭{@link java.io.Reader}
     *
     * @param in Reader
     * @return String
     */
    public String readToString(Reader in) throws IOException {
        StringBuffer buf = null;
        try {
            // 尝试从池中拿取对象
            buf =  pool.borrowObject();
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.append((char)c);
            }
            return buf.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to borrow buffer from pool :" + e.toString());
        } finally {
            try {
                in.close();
            } catch (Exception e){
                // ignored
            }
            try {
                if (null != buf){
                    // 归还对象
                    pool.returnObject(buf);
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    static class StringBufferFactory extends BasePooledObjectFactory<StringBuffer> {

        @Override
        public StringBuffer create() throws Exception {
            logger.debug(">>> create new Object from pool...");
            // 创建新对象
            return new StringBuffer();
        }

        /**
         * 使用{@link PooledObject}的实现包裹对象实例
         *
         * @param stringBuffer the instance to wrap
         *
         * @return The provided instance, wrapped by a {@link PooledObject}
         */
        @Override
        public PooledObject<StringBuffer> wrap(StringBuffer stringBuffer) {
            logger.debug(">>> wrap Object to PooledObject...");
            // 使用默认的PooledObject实现
            return new DefaultPooledObject<>(stringBuffer);
        }

        @Override
        public void activateObject(PooledObject<StringBuffer> p) throws Exception {
            logger.debug(">>> activate PooledObject...");
        }
        /**
         *
         * 将空闲对象返回到池中并且取消初始化
         *
         * @param p a {@code PooledObject} wrapping the instance to be passivated
         *
         * @throws Exception if there is a problem passivating <code>obj</code>,
         *    this exception may be swallowed by the pool.
         *
         * @see #destroyObject
         */
        @Override
        public void passivateObject(PooledObject<StringBuffer> p) throws Exception {
            logger.debug(">>> PooledObject return begin passivated...");
            // 清除buffer
            p.getObject().setLength(0);
        }

        @Override
        public void destroyObject(PooledObject<StringBuffer> p) throws Exception {
            logger.debug(">>> PooledObject destroy...");
        }

    }


    public static void main(String[] args) throws IOException {
        ReaderUtil readerUtil = new ReaderUtil();
        StringReader in = new StringReader("this is a test\nhello");
       logger.info(readerUtil.readToString(in));
    }
}
