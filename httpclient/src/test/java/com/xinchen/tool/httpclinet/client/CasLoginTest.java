package com.xinchen.tool.httpclinet.client;

import com.xinchen.tool.httpclinet.client.factory.CookieThreadHttpClientFactory;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author xinchen
 * @version 1.0
 * @date 16/07/2020 13:49
 */
public class CasLoginTest {

    static final String CAS_URL = "";
    static final String USERNAME = "";
    static final String PASSWORD = "";

    /**需要登录后才能访问的地址*/
    static final String APP_URL = "";


    @Test
    public void loginExecutor() throws IOException {
        final Executor executor = CasLogin.loginExecutor(CAS_URL, USERNAME, PASSWORD);
        System.out.println(executor.execute(Request.Get(APP_URL)).returnContent().asString());
    }

    @Test
    public void loginClient() throws IOException, URISyntaxException {
        final CloseableHttpClient client = CasLogin.loginClient(CAS_URL, USERNAME, PASSWORD);
        System.out.println(EntityUtils.toString(client.execute(new HttpGet(APP_URL)).getEntity()));
    }

    @Test
    public void loginFactory() throws IOException, URISyntaxException {
        final CookieThreadHttpClientFactory factory = CasLogin.loginFactory(CAS_URL, USERNAME, PASSWORD);
        System.out.println(EntityUtils.toString(factory.getClient().execute(new HttpGet(APP_URL)).getEntity()));
    }
}