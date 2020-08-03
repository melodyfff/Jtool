package com.xinchen.tool.fegin.metrics5;

import feign.Response.Body;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

/**
 * {@link Body} implementation that keeps track of how many bytes are read.
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/8/3 23:54
 */
public class MeteredBody implements Body {
    private final Body delegate;
    private Supplier<Long> count;

    public MeteredBody(Body body) {
        this.delegate = body;
        count = () -> 0L;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public Integer length() {
        return delegate.length();
    }

    @Override
    public boolean isRepeatable() {
        return delegate.isRepeatable();
    }

    @Override
    public InputStream asInputStream() throws IOException {
        // TODO, ideally, would like not to bring guava just for this
        final CountingInputStream input = new CountingInputStream(delegate.asInputStream());
        count = input::getCount;
        return input;
    }

    @Override
    public Reader asReader() throws IOException {
        return new InputStreamReader(asInputStream(), StandardCharsets.UTF_8);
    }

    public long count() {
        return count.get();
    }

    @Override
    public Reader asReader(Charset charset) throws IOException {
        return new InputStreamReader(asInputStream(), charset);
    }
}
