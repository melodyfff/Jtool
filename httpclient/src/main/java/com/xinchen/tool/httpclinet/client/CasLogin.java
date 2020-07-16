package com.xinchen.tool.httpclinet.client;

import com.xinchen.tool.httpclinet.client.factory.CookieThreadHttpClientFactory;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * 登录cas保持访问会话信息
 *
 *  fluent api 官网地址： http://hc.apache.org/httpcomponents-client-4.5.x/tutorial/html/fluent.html
 *
 * @author xinchen
 * @version 1.0
 * @date 15/07/2020 16:44
 */
public final class CasLogin {

    /**
     * 使用 fluent api 进行登录，并返回保持会话信息的{@link Executor}
     *
     * @param url cas地址
     * @param username 用户名
     * @param password 密码
     * @return 保持会话信息的{@link Executor}
     * @throws IOException IOException
     */
    static Executor loginExecutor(String url, String username, String password) throws IOException {
        // 主要保持cookie信息
        final BasicCookieStore basicCookieStore = new BasicCookieStore();
        // 还可使用CredentialsProvider()
        final Executor executor = Executor.newInstance().use(basicCookieStore);

        // 先访问一次拿到execution
        final String html = Request
                .Get(url)
                .execute().returnContent().asString();
        // 提取execution
        String execution = Jsoup.parse(html).select("input[name=execution]").first().attr("value");

        final String re = executor.execute(Request.Post(url)
                .bodyForm(Form.form()
                        .add("username", username)
                        .add("password", password)
                        .add("_eventId","submit")
                        .add("execution", execution)
                        .build()))
                .returnContent().asString();

        System.out.println(Jsoup.parse(re).select("h2").text());
        return executor;
    }

    /**
     * 获取保持登录信息的 {@link CloseableHttpClient}
     *
     * @param url cas地址
     * @param username 用户名
     * @param password 密码
     * @return {@link CloseableHttpClient}
     * @throws IOException
     * @throws URISyntaxException
     */
    static CloseableHttpClient loginClient(String url, String username, String password) throws IOException, URISyntaxException {
        final CloseableHttpClient client = new CookieThreadHttpClientFactory().getClient();
        innerLogin(url, username, password, client);
        return client;
    }


    /**
     * 获取保持登录信息的 {@link CookieThreadHttpClientFactory}
     *
     * @param url cas地址
     * @param username 用户名
     * @param password 密码
     * @return {@link CookieThreadHttpClientFactory}
     * @throws IOException IOException
     * @throws URISyntaxException URISyntaxException
     */
    static CookieThreadHttpClientFactory loginFactory(String url, String username, String password) throws IOException, URISyntaxException {
        final CookieThreadHttpClientFactory cookieThreadHttpClientFactory = new CookieThreadHttpClientFactory();
        final CloseableHttpClient client = cookieThreadHttpClientFactory.getClient();
        innerLogin(url, username, password, client);
        return cookieThreadHttpClientFactory;
    }

    /**
     *
     * 内部登录操作
     *
     * @param url cas地址
     * @param username 用户名
     * @param password 密码
     * @param client 保持会话信息的{@link Executor}
     * @throws IOException IOException
     * @throws URISyntaxException URISyntaxException
     */
    private static void innerLogin(String url, String username, String password, CloseableHttpClient client) throws IOException, URISyntaxException {
        try (final CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            final String html = EntityUtils.toString(response.getEntity());
            String execution = Jsoup.parse(html).select("input[name=execution]").first().attr("value");
            // 模拟登录cas
            HttpUriRequest login = RequestBuilder.post()
                    .setUri(new URI(url))
                    .addParameter("username", username)
                    .addParameter("password", password)
                    .addParameter("execution", execution)
                    .addParameter("_eventId", "submit")
                    .build();
            try (final CloseableHttpResponse re = client.execute(login)) {
                System.out.println(Jsoup.parse(EntityUtils.toString(re.getEntity())).select("h2").text());
            }
        }
    }
}
