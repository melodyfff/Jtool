package com.xinchen.tool.httptrace.framework.seata.integration.http;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2021-07-14 13:30
 */
class ServletMapping {
  public static List<ServletMapping> servletMappingList = new ArrayList<>();

  static {
    servletMappingList.add(new ServletMapping("/testGet", "testGet", "com.xinchen.tool.httptrace.framework.seata.integration.http.MockController"));
    servletMappingList.add(new ServletMapping("/testPost", "testPost", "com.xinchen.tool.httptrace.framework.seata.integration.http.MockController"));
    servletMappingList.add(new ServletMapping("/testException", "testException", "com.xinchen.tool.httptrace.framework.seata.integration.http.MockController"));
  }

  private String method;
  private String path;
  private String clazz;

  public ServletMapping(String path, String method, String clazz) {
    this.method = method;
    this.path = path;
    this.clazz = clazz;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }
}
