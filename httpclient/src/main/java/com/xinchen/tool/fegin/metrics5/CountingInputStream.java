package com.xinchen.tool.fegin.metrics5;

import feign.Util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copy from guava CountingInputStream
 * <p>
 * An {@link InputStream} that counts the number of bytes read.
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:41
 */
public class CountingInputStream extends FilterInputStream {

    private long count;
    private long mark = -1;

    /**
     * Wraps another input stream, counting the number of bytes read.
     *
     * @param in the input stream to be wrapped
     */
    public CountingInputStream(InputStream in) {
        super(Util.checkNotNull(in, "InputStream must not be null"));
    }

    /**
     * Returns the number of bytes read.
     */
    public long getCount() {
        return count;
    }

    @Override
    public int read() throws IOException {
        final int result = in.read();
        if (result != -1) {
            count++;
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int result = in.read(b, off, len);
        if (result != -1) {
            count += result;
        }
        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        final long result = in.skip(n);
        count += result;
        return result;
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
        mark = count;
        // it's okay to mark even if mark isn't supported, as reset won't work
    }

    @Override
    public synchronized void reset() throws IOException {
        if (!in.markSupported()) {
            throw new IOException("Mark not supported");
        }
        if (mark == -1) {
            throw new IOException("Mark not set");
        }

        in.reset();
        count = mark;
    }
}
