package com.xinchen.security.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
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
     * 默认阻止的参数，以空格分割
     * <p>
     * The set of Characters blocked by default on checked parameters.
     * Expressed as a whitespace delimited set of characters.
     */
    public static final String DEFAULT_CHARACTERS_BLOCKED = "? & # %";

    /**
     * 通过配置Filter init-param 的"parametersToCheck" 指定应该检查哪些请求参数,以空格分割，默认值为"*",允许所有参数
     * <p>
     * The name of the optional Filter init-param specifying what request parameters ought to be checked.
     * The value is a whitespace delimited set of parameters.
     * The exact value '*' has the special meaning of matching all parameters, and is the default behavior.
     */
    public static final String PARAMETERS_TO_CHECK = "parametersToCheck";

    /**
     * 通过配置Filter init-param 的"charactersToForbid" 指定指定已检查请求中禁止的字符参数，以空格分割的字符集
     * <p>
     * The name of the optional Filter init-param specifying what characters are forbidden in the checked request
     * parameters.  The value is a whitespace delimited set of such characters.
     */
    public static final String CHARACTERS_TO_FORBID = "charactersToForbid";

    /**
     * 通过配置Filter init-param 的"allowMultiValuedParameters" 指定是否允许检查的请求参数有多个值,此参数的允许值为"true"和"false"
     * 默认为false
     * <p>
     * The name of the optional Filter init-param specifying whether the checked request parameters are allowed
     * to have multiple values.  Allowable values for this init parameter are `true` and `false`.
     */
    public static final String ALLOW_MULTI_VALUED_PARAMETERS = "allowMultiValuedParameters";

    /**
     * 通过配置Filter init-param 的"onlyPostParameters" 指定应仅通过POST请求发送哪些请求参数
     * <p>
     * The name of the optional Filter init-param specifying what request parameters ought to be send via POST requests only.
     */
    public static final String ONLY_POST_PARAMETERS = "onlyPostParameters";

    /**
     * 要检查的参数名称集合，空集表示检查所有参数
     * <p>
     * Set of parameter names to check.
     * Empty set represents special behavior of checking all parameters.
     */
    private Set<String> parametersToCheck;

    /**
     * 在已检查的请求参数中禁止使用的字符集，空集表示不禁止任何字符
     * <p>
     * Set of characters to forbid in the checked request parameters.
     * Empty set represents not forbidding any characters.
     */
    private Set<Character> charactersToForbid;

    /**
     * 是否检查多值参数
     * <p>
     * Should checked parameters be permitted to have multiple values.
     */
    private boolean allowMultiValueParameters = false;

    /**
     * 仅允许通过的POST请求接收的参数集
     * <p>
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
        // 验证没有配置无法识别的init参数
        // 因为无法识别的init参数可能是尝试以重要方式配置此过滤器的采用者
        // 并意外忽略该意图可能会产生安全隐患。
        final Enumeration initParamNames = filterConfig.getInitParameterNames();
        throwIfUnrecognizedParamName(initParamNames);

        // 获取初始化参数
        final String initParamAllowMultiValuedParameters = filterConfig.getInitParameter(ALLOW_MULTI_VALUED_PARAMETERS);
        final String initParamParametersToCheck = filterConfig.getInitParameter(PARAMETERS_TO_CHECK);
        final String initParamOnlyPostParameters = filterConfig.getInitParameter(ONLY_POST_PARAMETERS);
        final String initParamCharactersToForbid = filterConfig.getInitParameter(CHARACTERS_TO_FORBID);

        this.allowMultiValueParameters = FilterUtils.parseStringToBooleanDefaultingToFalse(initParamAllowMultiValuedParameters);

        // 初始化需要检查的参数，支持通配符"*"
        try {
            this.parametersToCheck = parseParametersList(initParamParametersToCheck, true);
        } catch (final Exception e) {
            FilterUtils.logException(LOGGER, new ServletException("Error parsing request parameter " + PARAMETERS_TO_CHECK + " with value ["
                    + initParamParametersToCheck + "]", e));
        }

        // 初始化检查post参数,不支持通配符
        try {
            this.onlyPostParameters = parseParametersList(initParamOnlyPostParameters, false);
        } catch (final Exception e) {
            FilterUtils.logException(LOGGER, new ServletException("Error parsing request parameter " + ONLY_POST_PARAMETERS + " with value ["
                    + initParamOnlyPostParameters + "]", e));
        }

        // 初始化需要禁止的字符
        try {
            this.charactersToForbid = parseCharactersToForbid(initParamCharactersToForbid);
        } catch (final Exception e) {
            FilterUtils.logException(LOGGER, new ServletException("Error parsing request parameter " + CHARACTERS_TO_FORBID + " with value [" +
                    initParamCharactersToForbid + "]", e));
        }

        // 如果开启允许多值参数，但是要禁止的字符为空，则抛出异常
        if (this.allowMultiValueParameters && this.charactersToForbid.isEmpty()) {
            FilterUtils.logException(LOGGER, new ServletException("Configuration to allow multi-value parameters and forbid no characters makes "
                    + getClass().getSimpleName() + " a no-op, which is probably not what you want, " +
                    "so failing Filter init."));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 针对HttpServletRequest请求
            if (request instanceof HttpServletRequest){
                final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

                // immutable map from String param name --> String[] parameter values
                final Map<String,String[]> parameterMap = httpServletRequest.getParameterMap();

                // which parameters *on this request* ought to be checked.
                final Set<String> parametersToCheckHere;

                if (this.parametersToCheck.isEmpty()){
                    // 空集表示检查所有参数
                    // the special meaning of empty is to check *all* the parameters, so
                    parametersToCheckHere = parameterMap.keySet();
                } else {
                    parametersToCheckHere = this.parametersToCheck;
                }


                if (!this.allowMultiValueParameters){

                }


            }
        } catch (final Exception e){
            // translate to a ServletException to meet the typed expectations of the Filter API.
            FilterUtils.logException(LOGGER, new ServletException(getClass().getSimpleName() + " is blocking this request.  Examine the cause in" +
                    " this stack trace to understand why.", e));
        }
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


    /**
     * 解析以空格分隔的字符串
     * <p>
     * 如果String为null,返回空集
     * 如果String不包含标记，抛出 {@link IllegalArgumentException}
     * 如果唯一参数为 '*'，则返回空集
     * 如果在其他标记中遇到'*',抛出 {@link IllegalArgumentException}
     * <p>
     * 返回空集表示：检查所有参数
     * </p>
     *
     * @param initParamValue null, or a non-blank的以空格分隔的字符串
     * @param allowWildcard  是否允许使用通配符而不是参数列表
     * @return 一组要检查的参数的String名称，或一个表示check-them-all的空集
     * @throws IllegalArgumentException when the init param value is out of spec
     */
    static Set<String> parseParametersList(final String initParamValue, final boolean allowWildcard) {

        final Set<String> parameterNames = new HashSet<>();

        if (null == initParamValue) {
            return parameterNames;
        }

        // 以空格分隔
        final String[] tokens = initParamValue.split("\\s+");

        if (0 == tokens.length) {
            FilterUtils.logException(LOGGER, new IllegalArgumentException("[" + initParamValue +
                    "] had no tokens but should have had at least one token."));
        }

        // 开启通配符
        if (1 == tokens.length && "*".equals(tokens[0]) && allowWildcard) {
            return parameterNames;
        }

        for (final String parameterName : tokens) {
            if ("*".equals(parameterName)) {
                FilterUtils.logException(LOGGER, new IllegalArgumentException("Star token encountered among other tokens in parsing [" + initParamValue + "]"));
            }

            parameterNames.add(parameterName);
        }

        return parameterNames;
    }

    /**
     * 从String中解析以空格分隔的一组字符
     *
     * <p>
     * 如果String为null（未设置init param），则默认为DEFAULT_CHARACTERS_BLOCKED。
     * 如果String为“none”，则解析为空集意义块无字符。
     * 如果String为空抛，则避免configurer意外配置不阻止任何字符。
     * </p>
     *
     * @param paramValue 要解析的init参数的值
     * @return non-null Set 设置零个或多个要阻止的字符
     */
    static Set<Character> parseCharactersToForbid(String paramValue) {

        final Set<Character> charactersToForbid = new HashSet<Character>();

        if (null == paramValue) {
            // ? & # %
            paramValue = DEFAULT_CHARACTERS_BLOCKED;
        }

        if ("none".equals(paramValue)) {
            return charactersToForbid;
        }

        final String[] tokens = paramValue.split("\\s+");

        if (0 == tokens.length) {
            FilterUtils.logException(LOGGER, new IllegalArgumentException("Expected tokens when parsing [" + paramValue + "] but found no tokens."
                    + " If you really want to configure no characters, use the magic value 'none'."));
        }

        for (final String token : tokens) {
            if (token.length() > 1) {
                FilterUtils.logException(LOGGER, new IllegalArgumentException("Expected tokens of length 1 but found [" + token + "] when " +
                        "parsing [" + paramValue + "]"));
            }

            final Character character = token.charAt(0);
            charactersToForbid.add(character);
        }
        return charactersToForbid;
    }
}
