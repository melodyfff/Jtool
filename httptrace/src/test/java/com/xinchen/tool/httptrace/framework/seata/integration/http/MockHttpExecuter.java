package com.xinchen.tool.httptrace.framework.seata.integration.http;
import io.seata.core.context.RootContext;
import io.seata.integration.http.AbstractHttpExecutor;
import io.seata.integration.http.DefaultHttpExecutor;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.Args;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @date 2021-07-14 13:37
 */
class MockHttpExecuter extends AbstractHttpExecutor {

  DefaultHttpExecutor httpExecutor = DefaultHttpExecutor.getInstance();

  @Override
  public <K> K executeGet(String host, String path, Map<String, String> paramObject, Class<K> returnType) throws IOException {
    Args.notNull(host, "host");
    Args.notNull(path, "path");

    String getUrl = initGetUrl(host, path, paramObject);
    Map<String, String> headers = new HashMap<>();

    MockRequest mockRequest = new MockRequest(getUrl, headers, null, path, "get");
    MockResponse mockResponse = new MockResponse(null);
    String xid = RootContext.getXID();
    if (xid != null) {
      headers.put(RootContext.KEY_XID, xid);
    }
    MockWebServer webServer =  new MockWebServer();
    webServer.initServletMapping();
    return (K) webServer.dispatch(mockRequest, mockResponse);
  }

  @Override
  protected <T> void buildClientEntity(CloseableHttpClient httpClient, T paramObject) {

  }

  @Override
  protected <T> void buildGetHeaders(Map<String, String> headers, T paramObject) {

  }

  @Override
  protected String initGetUrl(String host, String path, Map<String, String> paramObject) {
    return httpExecutor.initGetUrl(host, path, paramObject);
  }

  @Override
  protected <T> void buildPostHeaders(Map<String, String> headers, T t) {

  }

  @Override
  protected <T> StringEntity buildEntity(StringEntity entity, T t) {
    return null;
  }

  @Override
  protected <K> K convertResult(HttpResponse response, Class<K> clazz) {
    return null;
  }

}
