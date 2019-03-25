package com.xinchen.security.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This is a Java Servlet Filter.
 * <p>
 * It examines specified Request Parameters as to whether they contain specified characters
 * and as to whether they are multivalued throws an Exception if they do not meet configured rules.
 *
 * <p>
 * Configuration:
 * 默认检查所有请求参数的哈希值、百分比、问号和&号 ，并且默认认为没有多值（enforcing no-multi-valued-ness）
 * 可以通过将init-param "allowMultiValuedParameters" 设置为"true" 来关闭多值检查，该值默认为"false",不支持其他字符串
 *
 * <p>
 * Initialization：
 *
 *
 *
 * </p>
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2019/3/26 0:09
 */
public class RequestParameterPolicyEnforcementFilter extends AbstractSecurityFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(RequestParameterPolicyEnforcementFilter.class.getName());


    /**
     *
     * 默认阻止的参数，以空格分割
     *
     * The set of Characters blocked by default on checked parameters.
     * Expressed as a whitespace delimited set of characters.
     */
    public static final String DEFAULT_CHARACTERS_BLOCKED = "? & # %";

    /**
     *
     * 通过配置Filter init-param 的"parametersToCheck" 指定应该检查哪些请求参数,以空格分割，默认值为"*",允许所有参数
     *
     * The name of the optional Filter init-param specifying what request parameters ought to be checked.
     * The value is a whitespace delimited set of parameters.
     * The exact value '*' has the special meaning of matching all parameters, and is the default behavior.
     */
    public static final String PARAMETERS_TO_CHECK = "parametersToCheck";

    /**
     *
     * 通过配置Filter init-param 的"charactersToForbid" 指定指定已检查请求中禁止的字符参数，以空格分割的字符集
     *
     * The name of the optional Filter init-param specifying what characters are forbidden in the checked request
     * parameters.  The value is a whitespace delimited set of such characters.
     */
    public static final String CHARACTERS_TO_FORBID = "charactersToForbid";

    /**
     *
     * 通过配置Filter init-param 的"allowMultiValuedParameters" 指定是否允许检查的请求参数有多个值,此参数的允许值为"true"和"false"
     * 默认为false
     *
     * The name of the optional Filter init-param specifying whether the checked request parameters are allowed
     * to have multiple values.  Allowable values for this init parameter are `true` and `false`.
     */
    public static final String ALLOW_MULTI_VALUED_PARAMETERS = "allowMultiValuedParameters";

    /**
     *
     * 通过配置Filter init-param 的"onlyPostParameters" 指定应仅通过POST请求发送哪些请求参数
     *
     * The name of the optional Filter init-param specifying what request parameters ought to be send via POST requests only.
     */
    public static final String ONLY_POST_PARAMETERS = "onlyPostParameters";

    /**
     *
     * 要检查的参数名称集合，空集表示检查所有参数
     *
     * Set of parameter names to check.
     * Empty set represents special behavior of checking all parameters.
     */
    private Set<String> parametersToCheck;

    /**
     *
     * 在已检查的请求参数中禁止使用的字符集，空集表示不禁止任何字符
     *
     * Set of characters to forbid in the checked request parameters.
     * Empty set represents not forbidding any characters.
     */
    private Set<Character> charactersToForbid;

    /**
     *
     * 是否检查多值参数
     *
     * Should checked parameters be permitted to have multiple values.
     */
    private boolean allowMultiValueParameters = false;

    /**
     *
     * 仅允许通过的POST请求接收的参数集
     *
     * Set of parameters which should be only received via POST requests.
     */
    private Set<String> onlyPostParameters;


    public void setParametersToCheck(Set<String> parametersToCheck) {
        this.parametersToCheck = parametersToCheck;
    }

    public void setCharactersToForbid(Set<Character> charactersToForbid) {
        this.charactersToForbid = charactersToForbid;
    }

    public void setAllowMultiValueParameters(boolean allowMultiValueParameters) {
        this.allowMultiValueParameters = allowMultiValueParameters;
    }

    public void setOnlyPostParameters(Set<String> onlyPostParameters) {
        this.onlyPostParameters = onlyPostParameters;
    }

    public RequestParameterPolicyEnforcementFilter() {
        // 配置日志
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
    }

    @Override
    public void setLoggerHandlerClassName(String loggerHandlerClassName) {
        super.setLoggerHandlerClassName(loggerHandlerClassName);
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
    }

    /* ========================================================================================================== */
    /* Filter methods */

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        FilterUtils.configureLogging(getLoggerHandlerClassName(), LOGGER);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }

    /* ========================================================================================================== */
    /* Init parameter parsing */

    static void throwIfUnrecognizedParamName(Enumeration initParamNames) throws ServletException {

    }

    /**
     * Returns the logger instance.
     *
     * @return Configured logger handler, or SLF4j handler, or JUL's default.
     */
    Logger getLogger() {
        return LOGGER;
    }
}
