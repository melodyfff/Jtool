package com.xinchen.tool.fegin;

import com.netflix.hystrix.HystrixCommand;
import feign.RequestLine;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author xinchen
 * @version 1.0
 * @date 08/09/2020 14:53
 */
public class ApiTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void test1(){
        server.enqueue(new MockResponse().setBody("hello"));
        server.enqueue(new MockResponse().setBody("world"));

        final TestApi testApi = Api.builder(TestApi.class, "http://localhost:"+server.getPort()).build();

        assertThat(testApi.call()).isEqualTo("hello");
        assertThat(testApi.hyCall().execute()).isEqualTo("world");


    }

    interface TestApi{
        @RequestLine("GET /hello")
        String call();

        @RequestLine("GET /hello")
        HystrixCommand<String> hyCall();
    }
}