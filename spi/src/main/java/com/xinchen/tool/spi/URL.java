package com.xinchen.tool.spi;

import com.xinchen.tool.spi.utils.ArrayUtils;
import com.xinchen.tool.spi.utils.CollectionUtils;
import com.xinchen.tool.spi.utils.NetUtils;
import com.xinchen.tool.spi.utils.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import static com.xinchen.tool.spi.constants.CommonConstants.*;

/**
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 11:53
 */
public class URL implements Serializable {
    private static final long serialVersionUID = -1994L;

    protected String protocol;

    protected String username;

    protected String password;

    /** by default, host to registry */
    protected String host;

    /** by default, port to registry */
    protected int port;

    protected String path;

    private final Map<String, String> parameters;

    private final Map<String, Map<String, String>> methodParameters;

    /** ex: 127.0.0.1:8080   127.0.0.1 */
    private transient String address;


    // ==== cache ====


    private volatile transient String ip;

    private volatile transient String toString;

    //----------------------------------------------------------
    // Construct
    //----------------------------------------------------------


    protected URL() {
        this.protocol = null;
        this.username = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.address = null;
        this.path = null;
        this.parameters = null;
        this.methodParameters = null;
    }

    public URL(String protocol, String host, int port) {
        this(protocol, null, null, host, port, null, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String[] pairs) { // varargs ... conflict with the following path argument, use array instead.
        this(protocol, null, null, host, port, null, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this(protocol, null, null, host, port, null, parameters);
    }

    public URL(String protocol, String host, int port, String path) {
        this(protocol, null, null, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String host, int port, String path, String... pairs) {
        this(protocol, null, null, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, null, null, host, port, path, parameters);
    }

    public URL(String protocol, String username, String password, String host, int port, String path) {
        this(protocol, username, password, host, port, path, (Map<String, String>) null);
    }

    public URL(String protocol, String username, String password, String host, int port, String path, String... pairs) {
        // pairs size must even.
        this(protocol, username, password, host, port, path, CollectionUtils.toStringMap(pairs));
    }

    public URL(String protocol, String username, String password, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, username, password, host, port, path, parameters, toMethodParameters(parameters));
    }

    public URL(String protocol,
               String username,
               String password,
               String host,
               int port,
               String path,
               Map<String, String> parameters,
               Map<String, Map<String, String>> methodParameters) {
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.address = getAddress(host,port);
        // trim the beginning "/"
        this.path = (null != path && path.startsWith("/")) ? path.substring(1) : path;
        this.parameters = Collections.unmodifiableMap((null == parameters) ? new HashMap<>() : new HashMap<>(parameters));
        this.methodParameters = Collections.unmodifiableMap(methodParameters);
    }

    /**
     * NOTICE: This method allocate too much objects, we can use {@link URLStrParser#parseDecodedStr(String)} instead.
     * <p>
     * Parse url string
     *
     * @param url URL string
     * @return URL instance
     * @see URL
     */
    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String username = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;
        // separator between body and parameters
        int i = url.indexOf('?');
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        String key = part.substring(0, j);
                        String value = part.substring(j + 1);
                        parameters.put(key, value);
                        // compatible with lower versions registering "default." keys
                        if (key.startsWith(DEFAULT_KEY_PREFIX)) {
                            parameters.putIfAbsent(key.substring(DEFAULT_KEY_PREFIX.length()), value);
                        }
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }
                protocol = url.substring(0, i);
                url = url.substring(i + 1);
            }
        }

        i = url.indexOf('/');
        if (i >= 0) {
            path = url.substring(i + 1);
            url = url.substring(0, i);
        }
        i = url.lastIndexOf('@');
        if (i >= 0) {
            username = url.substring(0, i);
            int j = username.indexOf(':');
            if (j >= 0) {
                password = username.substring(j + 1);
                username = username.substring(0, j);
            }
            url = url.substring(i + 1);
        }
        i = url.lastIndexOf(':');
        if (i >= 0 && i < url.length() - 1) {
            if (url.lastIndexOf('%') > i) {
                // ipv6 address with scope id
                // e.g. fe80:0:0:0:894:aeec:f37d:23e1%en0
                // see https://howdoesinternetwork.com/2013/ipv6-zone-id
                // ignore
            } else {
                port = Integer.parseInt(url.substring(i + 1));
                url = url.substring(0, i);
            }
        }
        if (url.length() > 0) {
            host = url;
        }

        return new URL(protocol, username, password, host, port, path, parameters);
    }


    public static String encode(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    //----------------------------------------------------------
    // Getter / Setter
    //----------------------------------------------------------

    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        return new URL(protocol, username, password, host, port, path, getParameters());
    }

    public String getHost() {
        return host;
    }

    public URL setHost(String host) {
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    /**
     * Fetch IP address for this URL.
     * <p>
     * Pls. note that IP should be used instead of Host when to compare with socket's address or to search in a map
     * which use address as its key.
     *
     * @return ip in string format
     */
    public String getIp() {
        if (ip == null) {
            ip = NetUtils.getIpByHost(host);
        }
        return ip;
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    public int getPort(int defaultPort) {
        return port <= 0 ? defaultPort : port;
    }

    public String getUsername() {
        return username;
    }

    public URL setUsername(String username) {
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    public String getPassword() {
        return password;
    }

    public URL setPassword(String password) {
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    public String getPath() {
        return path;
    }

    public URL setPath(String path) {
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    public String getAbsolutePath() {
        if (path != null && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public String getAddress() {
        if (address == null) {
            address = getAddress(host, port);
        }
        return address;
    }

    public URL setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
        return new URL(getProtocol(), username, password, host, port, path, getParameters());
    }

    public String getAuthority() {
        if (StringUtils.isEmpty(username)
                && StringUtils.isEmpty(password)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }

    public Map<String, Map<String, String>> getMethodParameters() {
        return methodParameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get the parameters to be selected(filtered)
     *
     * @param nameToSelect the {@link Predicate} to select the parameter name
     * @return non-null {@link Map}
     * @since 2.7.8
     */
    public Map<String, String> getParameters(Predicate<String> nameToSelect) {
        Map<String, String> selectedParameters = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            String name = entry.getKey();
            if (nameToSelect.test(name)) {
                selectedParameters.put(name, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(selectedParameters);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public URL addParameter(String key, String value) {
        if (StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return this;
        }
        // if value doesn't change, return immediately
        // value != null
        if (value.equals(getParameters().get(key))) {
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.put(key, value);

        return new URL(getProtocol(), username, password, host, port, path, map);
    }

    public URL addParameters(String... pairs) {
        if (pairs == null || pairs.length == 0) {
            return this;
        }
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Map pairs can not be odd number.");
        }
        Map<String, String> map = new HashMap<>();
        int len = pairs.length / 2;
        for (int i = 0; i < len; i++) {
            map.put(pairs[2 * i], pairs[2 * i + 1]);
        }
        return addParameters(map);
    }

    /**
     * Add parameters to a new url.
     *
     * @param parameters parameters in key-value pairs
     * @return A new URL
     */
    public URL addParameters(Map<String, String> parameters) {
        if (CollectionUtils.isEmptyMap(parameters)) {
            return this;
        }

        boolean hasAndEqual = true;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = getParameters().get(entry.getKey());
            if (value == null) {
                if (entry.getValue() != null) {
                    hasAndEqual = false;
                    break;
                }
            } else {
                if (!value.equals(entry.getValue())) {
                    hasAndEqual = false;
                    break;
                }
            }
        }
        // return immediately if there's no change
        if (hasAndEqual) {
            return this;
        }

        Map<String, String> map = new HashMap<>(getParameters());
        map.putAll(parameters);
        return new URL(getProtocol(), username, password, host, port, path, map);
    }

    public URL removeParameter(String key) {
        if (StringUtils.isEmpty(key)) {
            return this;
        }
        return removeParameters(key);
    }

    public URL removeParameters(String... keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        Map<String, String> map = new HashMap<>(getParameters());
        for (String key : keys) {
            map.remove(key);
        }
        if (map.size() == getParameters().size()) {
            return this;
        }
        return new URL(getProtocol(), username, password, host, port, path, map);
    }

    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }
        // no show username and password
        return toString = buildString(false, true);
    }

    //----------------------------------------------------------
    // Static Method
    //----------------------------------------------------------

    public static Map<String, Map<String, String>> toMethodParameters(Map<String, String> parameters) {
        Map<String, Map<String, String>> methodParameters = new HashMap<>();
        if (parameters == null) {
            return methodParameters;
        }

        // 寻找参数里面是否带有 methods=... 的值，有则存入
        String methodsString = parameters.get(METHODS_KEY);
        if (StringUtils.isNotEmpty(methodsString)) {
            List<String> methods = StringUtils.splitToList(methodsString, ',');
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey();
                for (int i = 0; i < methods.size(); i++) {
                    String method = methods.get(i);
                    int methodLen = method.length();
                    if (key.length() > methodLen
                            && key.startsWith(method)
                            //equals to: key.startsWith(method + '.')
                            && key.charAt(methodLen) == '.') {
                        String realKey = key.substring(methodLen + 1);
                        URL.putMethodParameter(method, realKey, entry.getValue(), methodParameters);
                    }
                }
            }
        } else {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey();
                int methodSeparator = key.indexOf('.');
                if (methodSeparator > 0) {
                    String method = key.substring(0, methodSeparator);
                    String realKey = key.substring(methodSeparator + 1);
                    URL.putMethodParameter(method, realKey, entry.getValue(), methodParameters);
                }
            }
        }
        return methodParameters;
    }

    public static void putMethodParameter(String method, String key, String value, Map<String, Map<String, String>> methodParameters) {
        Map<String, String> subParameter = methodParameters.computeIfAbsent(method, k -> new HashMap<>());
        subParameter.put(key, value);
    }

    private String buildString(boolean appendUser, boolean appendParameter, String... parameters) {
        return buildString(appendUser, appendParameter, false, false, parameters);
    }

    private String buildString(boolean appendUser, boolean appendParameter, boolean useIP, boolean useService, String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (StringUtils.isNotEmpty(protocol)) {
            buf.append(protocol);
            buf.append("://");
        }
        if (appendUser && StringUtils.isNotEmpty(username)) {
            buf.append(username);
            if (StringUtils.isNotEmpty(password)) {
                buf.append(":");
                buf.append(password);
            }
            buf.append("@");
        }
        String host;
        if (useIP) {
            host = getIp();
        } else {
            host = getHost();
        }
        if (StringUtils.isNotEmpty(host)) {
            buf.append(host);
            if (port > 0) {
                buf.append(":");
                buf.append(port);
            }
        }
        String path;
        path = getPath();
        // TODO service
//        if (useService) {
//            path = getServiceKey();
//        } else {
//            path = getPath();
//        }
        if (StringUtils.isNotEmpty(path)) {
            buf.append("/");
            buf.append(path);
        }

        if (appendParameter) {
            buildParameters(buf, true, parameters);
        }
        return buf.toString();
    }

    private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
        if (CollectionUtils.isNotEmptyMap(getParameters())) {
            List<String> includes = (ArrayUtils.isEmpty(parameters) ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<>(getParameters()).entrySet()) {
                if (StringUtils.isNotEmpty(entry.getKey())
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
    }

    private static String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }
}
