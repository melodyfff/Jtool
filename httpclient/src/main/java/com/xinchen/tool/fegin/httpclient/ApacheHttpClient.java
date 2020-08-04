package com.xinchen.tool.fegin.httpclient;

import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This module directs Feign's http requests to Apache's
 * <a href="https://hc.apache.org/httpcomponents-client-ga/">HttpClient</a>. Ex.
 *
 * <pre>
 * GitHub github = Feign.builder()
 *                  .client(new ApacheHttpClient())
 *                  .target(GitHub.class,"https://api.github.com");
 * </pre>
 *
 * @author xinchen
 * @version 1.0
 * @date 04/08/2020 08:55
 */
public class ApacheHttpClient implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";

    private final HttpClient client;

    public ApacheHttpClient() {
        this(HttpClientBuilder.create().build());
    }

    public ApacheHttpClient(HttpClient client) {
        this.client = client;
    }


    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HttpUriRequest httpUriRequest;
        try {
            httpUriRequest = toHttpUriRequest(request, options);
        } catch (URISyntaxException e) {
            throw new IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e);
        }
        HttpResponse response = client.execute(httpUriRequest);
        return toFeignResponse(response,request);
    }


    /**
     * 将feign请求request转换为apache HttpUriRequest
     * @param request Request
     * @param options Options
     * @return HttpUriRequest
     * @throws URISyntaxException URISyntaxException
     */
    private HttpUriRequest toHttpUriRequest(Request request, Request.Options options) throws URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());

        // 提取apache client中的RequestConfig,或者使用自定义的
        RequestConfig requestConfig =
                (client instanceof Configurable ? RequestConfig.copy(((Configurable) client).getConfig())
                        : RequestConfig.custom())
                        .setConnectTimeout(options.connectTimeoutMillis())
                        .setSocketTimeout(options.readTimeoutMillis())
                        .build();
        requestBuilder.setConfig(requestConfig);

        URI uri = new URIBuilder(request.url()).build();

        requestBuilder.setUri(uri.getScheme() + "://" + uri.getAuthority() + uri.getRawPath());

        // 构建请求参数
        // request query params
        List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, requestBuilder.getCharset());
        for (NameValuePair queryParam : queryParams) {
            requestBuilder.addParameter(queryParam);
        }

        // 构建请求header
        // request headers
        boolean hasAcceptHeader = false;
        for (Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }

            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                // The 'Content-Length' header is always set by the Apache client and it
                // doesn't like us to set it as well.
                continue;
            }

            for (String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        // some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }

        // request body
        if (request.body() != null) {
            HttpEntity entity;
            if (request.charset() != null) {
                ContentType contentType = getContentType(request);
                String content = new String(request.body(), request.charset());
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.body());
            }

            requestBuilder.setEntity(entity);
        } else {
            requestBuilder.setEntity(new ByteArrayEntity(new byte[0]));
        }

        return requestBuilder.build();
    }

    /**
     * 获取feign请求中的ContentType
     * @param request Request
     * @return ContentType
     */
    private ContentType getContentType(Request request) {
        ContentType contentType = null;
        for (Map.Entry<String, Collection<String>> entry : request.headers().entrySet()) {
            if ("Content-Type".equalsIgnoreCase(entry.getKey())) {
                Collection<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    contentType = ContentType.parse(values.iterator().next());
                    if (contentType.getCharset() == null) {
                        contentType = contentType.withCharset(request.charset());
                    }
                    break;
                }
            }
        }
        return contentType;
    }

    /**
     * 将apache的response转换为feign的Response
     *
     * @param httpResponse HttpResponse
     * @param request Request
     * @return Response
     */
    private Response toFeignResponse(HttpResponse httpResponse, Request request){
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        String reason = statusLine.getReasonPhrase();

        Map<String, Collection<String>> headers = new HashMap<>();
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();
            Collection<String> headerValues = headers.computeIfAbsent(name, k -> new ArrayList<>());
            headerValues.add(value);
        }

        return Response.builder()
                .status(statusCode)
                .reason(reason)
                .headers(headers)
                .request(request)
                .body(toFeignBody(httpResponse))
                .build();
    }

    /**
     * 将apache的response转换为feign的Response.Body
     *
     * @param httpResponse HttpResponse
     * @return Response.Body
     */
    private Response.Body toFeignBody(HttpResponse httpResponse) {
        final HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return new Response.Body() {

            @Override
            public Integer length() {
                return entity.getContentLength() >= 0 && entity.getContentLength() <= Integer.MAX_VALUE
                        ? (int) entity.getContentLength()
                        : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @SuppressWarnings("deprecation")
            @Override
            public Reader asReader() throws IOException {
                return new InputStreamReader(asInputStream(), UTF_8);
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null");
                return new InputStreamReader(asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}
