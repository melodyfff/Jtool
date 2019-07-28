package com.xinchen.security.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 允许用户轻松注入默认安全标头，以帮助保护应用程序。
 * 默认为包括以下标题：
 *
 * <pre>
 * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
 * Pragma: no-cache
 * Expires: 0
 * X-Content-Type-Options: nosniff
 * Strict-Transport-Security: max-age=15768000 ; includeSubDomains
 * X-Frame-Options: DENY
 * X-XSS-Protection: 1; mode=block
 * </pre>
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/7/28 23:07
 */
public class AddResponseHeadersFilter extends AbstractSecurityFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AddResponseHeadersFilter.class.getName());

    private Map<String, String> headersMap = new LinkedHashMap<>();

    public AddResponseHeadersFilter() {
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
    }

    public void setHeadersMap(final Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }

    @Override
    public void setLoggerHandlerClassName(final String loggerHandlerClassName) {
        super.setLoggerHandlerClassName(loggerHandlerClassName);
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
        final Enumeration initParamNames = filterConfig.getInitParameterNames();
        while (initParamNames.hasMoreElements()) {
            final String paramName = initParamNames.nextElement().toString();
            final String paramValue = filterConfig.getInitParameter(paramName);
            this.headersMap.put(paramName, paramValue);
        }
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        try {
            if (servletResponse instanceof HttpServletResponse) {
                final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                for (final Map.Entry<String, String> entry : this.headersMap.entrySet()) {
                    LOGGER.fine("Adding parameter " + entry.getKey() + " with value " + entry.getValue());
                    httpServletResponse.addHeader(entry.getKey(), entry.getValue());
                }
            }
        } catch (final Exception e) {
            FilterUtils.logException(LOGGER, e);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
