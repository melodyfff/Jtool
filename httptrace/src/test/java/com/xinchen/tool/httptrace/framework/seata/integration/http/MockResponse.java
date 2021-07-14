package com.xinchen.tool.httptrace.framework.seata.integration.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @date 2021-07-14 13:32
 */
class MockResponse {
  private OutputStream outputStream;

  public MockResponse(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public String write(String content) throws IOException {
    StringBuilder httpResponse = new StringBuilder();
    httpResponse.append("HTTP/1.1 200 OK\n")
        .append("Content-Type:application/json\n")
        .append("\r\n")
        .append(content);
    if (outputStream == null) {
      //local call
      return content;
    }
    else {
      outputStream.write(httpResponse.toString().getBytes());
      outputStream.close();
      return "success";
    }
  }
}
