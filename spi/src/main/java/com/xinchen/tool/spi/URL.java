package com.xinchen.tool.spi;

import com.xinchen.tool.spi.utlis.CollectionUtils;
import com.xinchen.tool.spi.utlis.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.xinchen.tool.spi.constants.CommonConstants.METHODS_KEY;

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

    private static String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }
}
