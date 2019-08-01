package com.xinchen.common.pool.examples;

import org.apache.commons.pool2.ObjectPool;

import java.io.IOException;
import java.io.Reader;

/**
 * http://commons.apache.org/proper/commons-pool/examples.html
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/8/1 22:28
 */
public class ReaderUtil {
    private ObjectPool<StringBuffer> pool;


    public ReaderUtil(ObjectPool<StringBuffer> pool) {
        this.pool = pool;
    }


    public String readToString(Reader in) throws IOException {
        StringBuffer buf = null;

        try {
            buf = pool.borrowObject();
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.append((char) c);
            }
            return buf.toString();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to borrow buffer from poll" + e.toString());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                // ignore
            }

            try {
                if (null != buf) {
                    // 用完归还
                    pool.returnObject(buf);
                }
            } catch (Exception e) {
                // ignore
            }

        }
    }


}
