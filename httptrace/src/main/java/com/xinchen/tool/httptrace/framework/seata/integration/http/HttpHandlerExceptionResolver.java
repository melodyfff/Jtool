package com.xinchen.tool.httptrace.framework.seata.integration.http;

import io.seata.core.context.RootContext;
import io.seata.integration.http.XidResource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * @date 2021-07-14 14:40
 */
class HttpHandlerExceptionResolver  extends AbstractHandlerExceptionResolver {


  @Override
  protected ModelAndView doResolveException(
      HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) {
    XidResource.cleanXid(request.getHeader(RootContext.KEY_XID));
    return null;
  }
}
