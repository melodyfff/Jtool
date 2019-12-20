package com.xinchen.tool.httpclinet.client.retry;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 *
 * 默认重试策略
 *
 * @author xinchen
 * @version 1.0
 * @date 20/12/2019 14:12
 */
public class DefaultRetryHandler implements HttpRequestRetryHandler {

    private static final int RETRY = 3;

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        // 尝试重试3次
        if (executionCount > RETRY){
            return false;
        }
        // 如果服务器丢掉了连接，那么就重试
        if (exception instanceof NoHttpResponseException) {
            return true;
        }
        // 不要重试SSL握手异常
        if (exception instanceof SSLHandshakeException) {
            return false;
        }
        // 超时不重试
        if (exception instanceof InterruptedIOException) {
            return false;
        }
        // 目标服务器不可达
        if (exception instanceof UnknownHostException) {
            return false;
        }
        // 连接被拒绝
        if (exception instanceof ConnectTimeoutException) {
            return false;
        }
        // SSL握手异常
        if (exception instanceof SSLException) {
            return false;
        }
        // 默认不重试
        return false;
    }
}
